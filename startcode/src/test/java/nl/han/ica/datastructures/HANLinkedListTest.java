package nl.han.ica.datastructures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HANLinkedListTest {


    @Test
    void testAddAndGetFirst() {
        HANLinkedList<String> list = new HANLinkedList<String>();
        list.addFirst("first");
        assertEquals("first", list.getFirst());
    }

    @Test
    void testAddAndGetFirst_NoHeader() {
        HANLinkedList<String> list = new HANLinkedList<String>();
        assertEquals(null, list.getFirst());
    }

    @Test
    void testAddAndGetFirst_LongerList() {
        HANLinkedList<String> list = new HANLinkedList<String>();
        list.addFirst("first");
        list.addFirst("second");
        assertEquals("second", list.getFirst());
        assertEquals("first", list.get(1));
    }

    @Test
    void testClear() {
        HANLinkedList<String> list = new HANLinkedList<String>();
        list.addFirst("first");
        list.insert(3, "second");
        list.clear();
        assertEquals(null, list.getFirst());
        assertEquals(null, list.getFirst());

    }

    @Test
    void testInsert() {
        HANLinkedList<String> list = new HANLinkedList<String>();
        list.addFirst("first");
        list.insert(1, "second");
        assertEquals("second", list.get(1));
    }

    @Test
    void testInsert_inbetween() {
        HANLinkedList<String> list = new HANLinkedList<String>();
        list.addFirst("first");
        list.insert(1, "second");
        list.insert(1, "second again");
        assertEquals("second again", list.get(1));
        assertEquals("second", list.get(2));
    }

    @Test
    void testInsert_skippedindex() {
        HANLinkedList<String> list = new HANLinkedList<String>();
        list.addFirst("first");
        list.insert(3, "second");
        assertEquals("second", list.get(1));
    }

    @Test
    void testInsert_header() {
        HANLinkedList<String> list = new HANLinkedList<String>();
        list.addFirst("first");
        list.insert(0, "second");
        assertEquals("second", list.getFirst());
    }

    @Test
    void testDelete() {
        HANLinkedList<String> list = new HANLinkedList<String>();
        list.addFirst("first");
        list.insert(1, "second");
        list.delete(1);
        assertEquals("first", list.get(0));
    }

    @Test
    void testDelete_deletefirst() {
        HANLinkedList<String> list = new HANLinkedList<String>();
        list.addFirst("first");
        list.insert(1, "second");
        list.delete(0);
        assertEquals("second", list.get(0));
    }

    @Test
    void testDelete_deleteoutofbounds() {
        HANLinkedList<String> list = new HANLinkedList<String>();
        list.addFirst("first");
        list.insert(1, "second");
        list.delete(4); //negatieve getallen verwijderen het eerste element
        assertEquals("second", list.get(1));
    }

    @Test
    void testGet() {
        HANLinkedList<String> list = new HANLinkedList<String>();
        list.addFirst("first");
        assertEquals("first", list.get(0));
    }

    @Test
    void testGet_outofbounds() {
        HANLinkedList<String> list = new HANLinkedList<String>();
        list.addFirst("first");
        assertNull(list.get(4));
    }

    @Test
    void testRemoveFirst() {
        HANLinkedList<String> list = new HANLinkedList<String>();
        list.addFirst("first");
        list.insert(1, "second");
        list.removeFirst();
        assertEquals("second", list.getFirst());
    }

    @Test
    void testRemoveFirst_header() {
        HANLinkedList<String> list = new HANLinkedList<String>();
        list.addFirst("first");
        list.removeFirst();
        assertNull(list.getFirst());
    }

    @Test
    void testGetFirst() {
        HANLinkedList<String> list = new HANLinkedList<String>();
        list.addFirst("first");
        assertEquals("first", list.getFirst());
    }

    @Test
    void testGetFirst_noheader() {
        HANLinkedList<String> list = new HANLinkedList<String>();
        assertNull(list.getFirst());
    }

    @Test
    void getSize() {
        HANLinkedList<String> list = new HANLinkedList<String>();
        list.addFirst("first");
        list.insert(1, "second");
        assertEquals(2, list.getSize());
    }
}