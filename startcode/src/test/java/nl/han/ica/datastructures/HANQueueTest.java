package nl.han.ica.datastructures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HANQueueTest {
    
    @Test
    void testIsEmpty() {
        HANQueue<String> queue = new HANQueue<>();
        assertTrue(queue.isEmpty());
    }

    @Test
    void testIsEmpty_notempty() {
        HANQueue<String> queue = new HANQueue<>();
        queue.enqueue("first");
        assertFalse(queue.isEmpty());
    }

    @Test
    void testEnqueue() {
        HANQueue<String> queue = new HANQueue<>();
        queue.enqueue("first");
        assertEquals("first", queue.peek());
    }

    @Test
    void testDequeue() {
        HANQueue<String> queue = new HANQueue<>();
        queue.enqueue("first");
        queue.enqueue("second");
        assertEquals("first", queue.dequeue());
        assertEquals("second", queue.peek());
    }

    @Test
    void testDequeue_empty() {
        HANQueue<String> queue = new HANQueue<>();
        assertNull(queue.dequeue());
    }

    @Test
    void testClear() {
        HANQueue<String> queue = new HANQueue<>();
        queue.enqueue("first");
        queue.enqueue("second");
        queue.clear();
        assertTrue(queue.isEmpty());
    }

    @Test
    void testPeek() {
        HANQueue<String> queue = new HANQueue<>();
        queue.enqueue("first");
        queue.enqueue("second");
        assertEquals("first", queue.peek());
        queue.dequeue();
        assertEquals("second", queue.peek());
    }

    @Test
    void testPeek_empty() {
        HANQueue<String> queue = new HANQueue<>();
        assertNull(queue.peek());
    }

    @Test
    void getSize() {
        HANQueue<String> queue = new HANQueue<>();
        queue.enqueue("first");
        queue.enqueue("second");
        assertEquals(2, queue.getSize());
    }
}