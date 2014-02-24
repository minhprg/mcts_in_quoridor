"""Provide some widely useful utilities. "from utils import *".

"""
import bisect

#______________________________________________________________________________
# Queues: Stack, FIFOQueue, PriorityQueue

class Queue:
    """Queue is an abstract class/interface. There are three types:
        Stack(): A Last In First Out Queue.
        FIFOQueue(): A First In First Out Queue.
        PriorityQueue(lt): Queue where items are sorted by lt, (default <).
    Each type supports the following methods and functions:
        q.append(item)  -- add an item to the queue
        q.extend(items) -- equivalent to: for item in items: q.append(item)
        q.pop()         -- return the top item from the queue
        len(q)          -- number of items in q (also q.__len())
    Note that isinstance(Stack(), Queue) is false, because we implement stacks
    as lists.  If Python ever gets interfaces, Queue will be an interface."""

    def __init__(self): 
        abstract

    def extend(self, items):
        for item in items: self.append(item)

def Stack():
    """Return an empty list, suitable as a Last-In-First-Out Queue."""
    return []

class FIFOQueue(Queue):
    """A First-In-First-Out Queue."""
    def __init__(self):
        self.A = []; self.start = 0
    def append(self, item):
        self.A.append(item)
    def __len__(self):
        return len(self.A) - self.start
    def extend(self, items):
        self.A.extend(items)     
    def pop(self):        
        e = self.A[self.start]
        self.start += 1
        if self.start > 5 and self.start > len(self.A)/2:
            self.A = self.A[self.start:]
            self.start = 0
        return e

class PriorityQueueElmt:
    """ The elements of the priority queue """
    def __init__(self,val,e):
        self.val = val
        self.e = e
    
    def __lt__(self,other):
        return self.val < other.val
    
    def value(self):
        return self.val
    
    def elem(self):
        return self.e
        

class PriorityQueue(Queue):
    """A queue in which the minimum (or maximum) element (as determined by f and
    order) is returned first. If order is min, the item with minimum f(x) is
    returned first; if order is max, then it is the item with maximum f(x)."""
    def __init__(self, f, order=min):
        self.A=[]
        self.order=order
        self.f=f
    def append(self, item):
        queueElmt = PriorityQueueElmt(self.f(item),item)
        bisect.insort(self.A, queueElmt)
    def __len__(self):
        return len(self.A)
    def pop(self):
        if self.order == min:
            return self.A.pop(0).elem()
        else:
            return self.A.pop().elem()