package nl.han.ica.datastructures;

public class HANLinkedList<T> implements IHANLinkedList<T> {

    private ListNode<T> header;

    @Override
    public void addFirst(T value) {
        if(header != null){
            ListNode<T> node = new ListNode<T>(value);
            node.setNext(header);
            header = node;
        } else {
            header = new ListNode<T>(value);
        }
    }

    @Override
    public void clear() {
        header = null;
    }

    @Override
    public void insert(int index, T value) {
        ListNode<T> node = header;
        ListNode<T> nieuweNode = new ListNode<T>(value);
        ListNode<T> lastNode = null;
        ListNode<T> lastFullNode = null;

        int i=0;

        if(index == 0){
            addFirst(value);
            return;
        }

        while(i < index && node != null){
            i++;
            if(node.getNext() == null){
                lastFullNode = node;
            }
            lastNode = node;
            node = node.getNext();

        }

        if (node != null) {
            //aanname dat de lijst opschuift wanneer er een node wordt toegevoegd tussen 2 bestaande nodes
            nieuweNode.setNext(node);
            lastNode.setNext(nieuweNode);
        } else {
            lastFullNode.setNext(nieuweNode);
        }
    }

    @Override
    public void delete(int pos) {
        ListNode<T> node = header;
        ListNode<T> previous = null;
        int i=0;
        while(i < pos && node != null){
            i++;
            previous = node;
            node = node.getNext();
        }

        if (node != null) {                 //bestaande node gevonden
            if(previous == null){           //header
                removeFirst();              //verwijder eerste node (bij pos <= 0)
                return;
            }
            if (node.getNext() == null) {   //geen volgende node
                previous.setNext(null);
            } else {
                previous.setNext(node.getNext());
            }
        }
    }

    @Override
    public T get(int pos) {
        ListNode<T> node = header;
        int i=0;
        while(i < pos && node != null){
            i++;
            node = node.getNext();
        }
        if (node != null) {
            return node.getValue();
        }
        return null;
    }

    @Override
    public void removeFirst() {
        if(header == null){
            return;
        }
        if(header.getNext() == null){
            header = null;
            return;
        }
        header = header.getNext();
    }

    @Override
    public T getFirst() {
        if(header == null){
            return null;
        }
        return header.getValue();
    }

    @Override
    public int getSize() {
        ListNode<T> node = header;
        int i=0;
        while(node != null){
            i++;
            node = node.getNext();
        }
        return i;
    }
}
