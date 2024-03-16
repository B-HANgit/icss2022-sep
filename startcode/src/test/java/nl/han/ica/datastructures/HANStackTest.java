package nl.han.ica.datastructures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HANStackTest {

    @Test
    void testPushAndPeek() {
        HANStack<String> stack = new HANStack<>();
        stack.push("first");
        assertEquals("first", stack.peek());
    }

    @Test
    void testPushAndPeek_multiple() {
        HANStack<String> stack = new HANStack<>();
        stack.push("first");
        stack.push("second");
        assertEquals("second", stack.peek());
        stack.push("third");
        assertEquals("third", stack.peek());
    }

    @Test
    void testPopAndPeek() {
        HANStack<String> stack = new HANStack<>();
        stack.push("first");
        stack.push("second");
        stack.pop();
        assertEquals("first", stack.peek());
    }

    @Test
    void testPopAndPeek_emptystack() {
        HANStack<String> stack = new HANStack<>();
        stack.pop();
        assertNull(stack.peek());
    }

    @Test
    void testPopAndPeek_multiple() {
        HANStack<String> stack = new HANStack<>();
        stack.push("first");
        stack.push("second");
        stack.pop();
        assertEquals("first", stack.peek());
        stack.pop();
        assertNull(stack.peek());
    }
}