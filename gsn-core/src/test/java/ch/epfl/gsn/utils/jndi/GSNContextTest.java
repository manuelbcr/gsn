package ch.epfl.gsn.utils.jndi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;

import org.junit.Test;

public class GSNContextTest {

    @Test(expected = OperationNotSupportedException.class)
    public void testAddToEnvironment() throws NamingException {
        GSNContext context = new GSNContext();  
        context.addToEnvironment("propName", new Object());

    }

    @Test
    public void testBindAndLookup() throws NamingException {
        GSNContext context = new GSNContext();
        context.bind("name", new Object());
        assertNotNull(context.lookup("name"));
    }

    @Test(expected = OperationNotSupportedException.class)
    public void testBind2() throws NamingException {
        GSNContext context = new GSNContext();
        context.bind((Name) null, new Object());
    }

    @Test(expected = OperationNotSupportedException.class)
    public void testClose() throws NamingException {
        GSNContext context = new GSNContext();  
        context.close();
    }

   @Test(expected = OperationNotSupportedException.class)
    public void testComposeName() throws NamingException {
        GSNContext context = new GSNContext();
        context.composeName((Name) null, (Name) null);
    }

    @Test(expected = OperationNotSupportedException.class)
    public void testComposeName2() throws NamingException {
        GSNContext context = new GSNContext();
        context.composeName("name", "prefix");
    }

   @Test(expected = OperationNotSupportedException.class)
    public void testCreateSubcontext() throws NamingException {
        GSNContext context = new GSNContext();
        context.createSubcontext("name");
    }

    @Test(expected = OperationNotSupportedException.class)
    public void testCreateSubcontext2() throws NamingException {
        GSNContext context = new GSNContext();
        context.createSubcontext((Name) null);
    }

    @Test(expected = OperationNotSupportedException.class)
    public void testDestroySubcontext() throws NamingException {
        GSNContext context = new GSNContext();
        context.destroySubcontext("name");
    }

    @Test(expected = OperationNotSupportedException.class)
    public void testDestroySubcontext2() throws NamingException {
        GSNContext context = new GSNContext();
        context.destroySubcontext((Name)null);
    }

    @Test(expected = OperationNotSupportedException.class)
    public void testGetEnvironment() throws NamingException {
        GSNContext context = new GSNContext();
        context.getEnvironment();
    }

    @Test
    public void testGetMainContext() {
       assertNotNull(GSNContext.getMainContext());

    }

    @Test
    public void testGetNameInNamespace() throws NamingException {
        GSNContext context = new GSNContext();
        assertNotNull(context.getNameInNamespace());
        assertEquals("", context.getNameInNamespace());
    }

    @Test(expected = OperationNotSupportedException.class)
    public void testGetNameParser() throws NamingException {
        GSNContext context = new GSNContext();
        context.getNameParser("name");
    }

    @Test(expected = OperationNotSupportedException.class)
    public void testGetNameParser2() throws NamingException{
        GSNContext context = new GSNContext();
        context.getNameParser((Name) null);
    }

    @Test(expected = OperationNotSupportedException.class)
    public void testList() throws NamingException {
        GSNContext context = new GSNContext();
        context.list((Name) null);
    }

    @Test(expected = OperationNotSupportedException.class)
    public void testList2() throws NamingException {
        GSNContext context = new GSNContext();
        context.list("name");
    }

    @Test(expected = OperationNotSupportedException.class)
    public void testListBindings() throws NamingException {
        GSNContext context = new GSNContext();
        context.listBindings("name");
    }

   @Test(expected = OperationNotSupportedException.class)
    public void testListBindings2() throws NamingException {
        GSNContext context = new GSNContext();
        context.listBindings((Name)null);
    }

    @Test(expected = OperationNotSupportedException.class)
    public void testLookup() throws NamingException {
        GSNContext context = new GSNContext();
        context.lookup((Name)null);
    }

    @Test(expected = OperationNotSupportedException.class)
    public void testLookupLink() throws NamingException{
        GSNContext context = new GSNContext();
        context.lookupLink("name");
    }

    @Test(expected = OperationNotSupportedException.class)
    public void testLookupLink2() throws NamingException{
        GSNContext context = new GSNContext();
        context.lookupLink((Name)null);
    }

    @Test
    public void testRebind() throws NamingException {
        GSNContext context = new GSNContext();
        context.bind("name", new Object());
        assertNotNull(context.lookup("name"));
        context.rebind("name", new Object());
        assertNotNull(context.lookup("name"));
    }

    @Test(expected = OperationNotSupportedException.class)
    public void testRebind2() throws NamingException {
        GSNContext context = new GSNContext();
        context.rebind((Name) null, new Object());
    }

    @Test(expected = OperationNotSupportedException.class)
    public void testRemoveFromEnvironment() throws NamingException {
        GSNContext context = new GSNContext();
        context.removeFromEnvironment("propName");
    }

    @Test(expected = OperationNotSupportedException.class)
    public void testRename() throws NamingException{
        GSNContext context = new GSNContext();
        context.rename((Name)null,(Name)null);
    }

    @Test(expected = OperationNotSupportedException.class)
    public void testRename2() throws NamingException{
        GSNContext context = new GSNContext();
        context.rename("name", "name");
    }

    @Test(expected = OperationNotSupportedException.class)
    public void testUnbind() throws NamingException{
        GSNContext context = new GSNContext();
        context.unbind((Name) null);
    }

    @Test
    public void testUnbind2() throws NamingException{
        GSNContext context = new GSNContext();
        context.bind("name",new Object());
        assertNotNull(context.lookup("name"));
        context.unbind("name");
        assertNull(context.lookup("name"));
    }
}
