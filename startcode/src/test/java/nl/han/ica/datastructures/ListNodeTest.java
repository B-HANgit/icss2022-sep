package nl.han.ica.datastructures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ListNodeTest {

    ListNode<Integer> node = new ListNode<>(5);


    @Test
    void testGetValue() {
        assertEquals(5, node.getValue());
    }

    @Test
    void testSetValue() {
        node.setValue(10);
        assertEquals(10, node.getValue());
    }

    @Test
    void testSetAndGetNext() {
        ListNode<Integer> nextNode = new ListNode<>(10);
        node.setNext(nextNode);
        assertEquals(10, nextNode.getValue());
    }

}