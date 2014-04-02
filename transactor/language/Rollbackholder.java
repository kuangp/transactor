package transactor.language;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;

import java.util.Vector;
import java.util.Hashtable;

import salsa.naming.UAN;
import salsa.naming.UAL;

import salsa.resources.OutputService;
import salsa.resources.ErrorService;
import gc.SystemMessage;
import gc.WeakReference;

import salsa.language.*;

// This class extends the Placeholder class to allow for anonymous actors 
public class Rollbackholder extends Placeholder {
    private UAN __uan;
    private UAL __ual;

    boolean dead = false;

    public Rollbackholder(UAN uan, UAL ual) {
        super(uan, ual);
        __uan = uan;
        __ual = ual;
        RunTime.createdUniversalActor();
    }

    public synchronized void putMessageInMailbox(Message message) {
        if (dead) {
            WeakReference self = new WeakReference(__uan, __ual);
            self.send(message);
        }
        else {mailbox.addElement(message);}
    }

    public synchronized void putMessageInMailbox(SystemMessage message) {
        if (dead) {
            WeakReference self = new WeakReference(__uan, __ual);
            self.send(message);
        }
        else {mailbox.addElement(message);}
    }

    public synchronized void sendAllMessages() {
        WeakReference self = new WeakReference(__uan, __ual);
        Message sendPlaceholderMsg;
        Object[] args = { mailbox.toArray()};
        sendPlaceholderMsg = new Message(null, self, "getPlaceholderMsg",args, false);
        self.send(sendPlaceholderMsg);
        mailbox.clear();
        dead= true;
    }
}

