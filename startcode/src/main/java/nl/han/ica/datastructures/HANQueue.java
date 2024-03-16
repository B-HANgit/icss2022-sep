package nl.han.ica.datastructures;

public class HANQueue<T> implements IHANQueue<T> {

    public HANLinkedList<T> list = new HANLinkedList<T>();

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean isEmpty() {
        return list.getSize() == 0;
    }

    @Override
    public void enqueue(T value) {
        if(list.getSize() == 0){
            list.addFirst(value);
        } else {
            int i = list.getSize();
            list.insert(i, value);
        }
    }

    @Override
    public T dequeue() {
        T val = list.getFirst();
        list.removeFirst();
        return val;
    }

    @Override
    public T peek() {
        return list.getFirst();
    }

    @Override
    public int getSize() {
        return list.getSize();
    }
}
