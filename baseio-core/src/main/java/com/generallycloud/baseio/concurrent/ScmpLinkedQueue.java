package com.generallycloud.baseio.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

public class ScmpLinkedQueue<T extends Linkable<T>> implements LinkedQueue<T>{

	protected Linkable<T>	head	= null;				// volatile ?
	protected Lock		lock;
	protected AtomicInteger	size	= new AtomicInteger();
	protected Linkable<T>	tail	= null;				// volatile ?

	public ScmpLinkedQueue(Linkable<T> linkable) {
		this(linkable, new ReentrantLockImpl());
	}

	public ScmpLinkedQueue(Linkable<T> linkable, Lock lock) {
		linkable.setValidate(false);
		this.head = linkable;
		this.tail = linkable;
		this.lock = lock;
	}

	private T get(Linkable<T> h) {
		if (h.isValidate()) {
			Linkable<T> next = h.getNext();
			if (next == null) {
				h.setValidate(false);
			} else {
				head = next;
			}
			this.size.decrementAndGet();
			return h.getValue();
		} else {
			Linkable<T> next = h.getNext();
			head = next;
			return get(next);
		}
	}

	public void offer(Linkable<T> object) {
		Lock lock = this.lock;
		lock.lock();
		try {
			tail.setNext(object);
			tail = object;
		} finally {
			lock.unlock();
		}
		size.incrementAndGet();
	}

	public T poll() {
		int size = size();
		if (size == 0) {
			return null;
		}
		return get(head);
	}

	public int size() {
		return size.get();
	}

}
