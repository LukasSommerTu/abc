import java.util.*;

class C {
    {
        Object[] array;

        /*[*/List l1 = null;
        l1 = new ArrayList(Arrays.asList(array));/*]*/

        List l2 = null;
        l2 = new ArrayList(Arrays.asList(getObjects()));

        System.out.println("l1 = " + l1 + ", l2 = " + l2);
    }


    String[] getObjects() {
    }
}