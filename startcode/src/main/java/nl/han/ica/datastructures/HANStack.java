package nl.han.ica.datastructures;

public class HANStack<T> implements IHANStack<T>{
    private HANLinkedList<T> list = new HANLinkedList<T>();

    @Override
    public void push(T value) {
        if(list.getSize() == 0){
            list.addFirst(value);
        } else {
            int i = list.getSize();
            list.insert(i, value);
        }
    }

    @Override
    public T pop() {
        if(list.getSize() == 0){
            return null;
        }
        int pos = list.getSize()-1;
        T val = list.get(pos);
        list.delete(pos);
        return val;
    }

    @Override
    public T peek() {
        return list.get(list.getSize()-1);
    }
}
