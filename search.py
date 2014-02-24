"""Search (Chapters 3-4)

The way to use this code is to subclass Problem to create a class of problems,
then create problem instances and solve them with calls to the various search
functions."""

from utils import *
import sys
import math
import random

#______________________________________________________________________________

class Problem:
    """The abstract class for a formal problem.  You should subclass this and
    implement the method successor, and possibly __init__, goal_test, and
    path_cost. Then you will create instances of your subclass and solve them
    with the various search functions."""

    def __init__(self, initial, goal=None):
        """The constructor specifies the initial state, and possibly a goal
        state, if there is a unique goal.  Your subclass's constructor can add
        other arguments."""
        self.initial = initial; self.goal = goal
        
    def successor(self, state):
        """Given a state, return a sequence of (action, state) pairs reachable
        from this state. If there are many successors, consider an iterator
        that yields the successors one at a time, rather than building them
        all at once. Iterators will work fine within the framework."""
        abstract
    
    def goal_test(self, state):
        """Return True if the state is a goal. The default method compares the
        state to self.goal, as specified in the constructor. Implement this
        method if checking against a single self.goal is not enough."""
        return state == self.goal
    
    def path_cost(self, c, state1, action, state2):
        """Return the cost of a solution path that arrives at state2 from
        state1 via action, assuming cost c to get up to state1. If the problem
        is such that the path doesn't matter, this function will only look at
        state2.  If the path does matter, it will consider c and maybe state1
        and action. The default method costs 1 for every step in the path."""
        return c + 1

    def value(self):
        """For optimization problems, each state has a value.  Hill-climbing
        and related algorithms try to maximize this value."""
        abstract
#______________________________________________________________________________
    
class Node:
    """A node in a search tree. Contains a pointer to the parent (the node
    that this is a successor of) and to the actual state for this node. Note
    that if a state is arrived at by two paths, then there are two nodes with
    the same state.  Also includes the action that got us to this state, and
    the total path_cost (also known as g) to reach the node.  Other functions
    may add an f and h value; see best_first_graph_search and astar_search for
    an explanation of how the f and h values are handled. You will not need to
    subclass this class."""

    def __init__(self, state, parent=None, action=None, path_cost=0):
        "Create a search tree Node, derived from a parent by an action."
        self.state=state
        self.parent = parent
        self.action=action
        self.path_cost=path_cost
        if parent:
            self.depth = parent.depth + 1
        else:
            self.depth = 0
            
    def __repr__(self):
        return "<Node %s>" % (self.state,)
    
    def path(self):
        "Create a list of nodes from the root to this node."
        x, result = self, [self]
        while x.parent:
            result.append(x.parent)
            x = x.parent
        return result

    def expand(self, problem):
        "Yield the nodes reachable from this node. [Fig. 3.8]"
        for (act,next) in problem.successor(self.state):

            yield Node(next, self, act,
                problem.path_cost(self.path_cost, self.state, act, next))


#______________________________________________________________________________
## Uninformed Search algorithms

def tree_search(problem, fringe):
    """Search through the successors of a problem to find a goal.
    The argument fringe should be an empty queue.
    Don't worry about repeated paths to a state. [Fig. 3.8]"""
    fringe.append(Node(problem.initial))
    while fringe:
        node = fringe.pop()
        if problem.goal_test(node.state):
            return node
        fringe.extend(node.expand(problem))
    return None

def breadth_first_tree_search(problem):
    "Search the shallowest nodes in the search tree first. [p 74]"
    return tree_search(problem, FIFOQueue())
    
def depth_first_tree_search(problem):
    "Search the deepest nodes in the search tree first. [p 74]"
    return tree_search(problem, Stack())

def graph_search(problem, fringe):
    """Search through the successors of a problem to find a goal.
    The argument fringe should be an empty queue.
    If two paths reach a state, only use the best one. [Fig. 3.18]"""
    closed = {}
    fringe.append(Node(problem.initial))
    while fringe:
        node = fringe.pop()
        if problem.goal_test(node.state): 
            return node
        if node.state not in closed:
            closed[node.state] = True
            fringe.extend(node.expand(problem))    
    return None

def breadth_first_graph_search(problem):
    "Search the shallowest nodes in the search tree first. [p 74]"
    return graph_search(problem, FIFOQueue())
    
def depth_first_graph_search(problem):
    "Search the deepest nodes in the search tree first. [p 74]"
    return graph_search(problem, Stack())

def depth_limited_search(problem, limit=50):
    "[Fig. 3.12]"
    def recursive_dls(node, problem, limit):
        cutoff_occurred = False
        if problem.goal_test(node.state):
            return node
        elif node.depth == limit:
            return 'cutoff'
        else:
            for successor in node.expand(problem):
                result = recursive_dls(successor, problem, limit)
                if result == 'cutoff':
                    cutoff_occurred = True
                elif result != None:
                    return result
        if cutoff_occurred:
            return 'cutoff'
        else:
            return None
    # Body of depth_limited_search:
    return recursive_dls(Node(problem.initial), problem, limit)

def iterative_deepening_search(problem):
    "[Fig. 3.13]"
    for depth in range(sys.maxsize):
        result = depth_limited_search(problem, depth)
        if result is not 'cutoff':
            return result






#______________________________________________________________________________
# Informed (Heuristic) Search

def best_first_graph_search(problem, f):
    """Search the nodes with the lowest f scores first.
    You specify the function f(node) that you want to minimize; for example,
    if f is a heuristic estimate to the goal, then we have greedy best
    first search; if f is node.depth then we have depth-first search."""
    return graph_search(problem, PriorityQueue(f,min))

def astar_graph_search(problem, h):
    """A* search is best-first graph search with f(n) = g(n)+h(n).
    You need to specify the h function when you call astar_search."""
    def f(n):
        return n.path_cost + h(n)
    return best_first_graph_search(problem, f)






#____________________________________________________________________________
# Local Search Algorithms

class LSNode:
    """A node in a local search. You will not need to subclass this class 
        for local search."""

    def __init__(self, problem, state, step):
        """Create a local search Node."""
        self.problem = problem
        self.state = state
        self.step = step
        self._value = None

    def __repr__(self):
        return "<Node %s>" % (self.state,)

    def value(self):
        """Returns the value of the state contained in this node."""
        if self._value is None:
            self._value = self.problem.value(self.state)
        return self._value

    def expand(self):
        """Yields nodes reachable from this node. [Fig. 3.8]"""
        for (act, next) in self.problem.successor(self.state):
            yield LSNode(self.problem, next, self.step + 1)




def random_walk(problem, limit=100, callback=None):
    """Perform a random walk in the search space and return the best solution
    found. The returned value is a Node.
    If callback is not None, it must be a one-argument function that will be
    called at each step with the current node.
    """
    current = LSNode(problem, problem.initial, 0)
    best = current
    for step in range(limit):
        if callback is not None:
            callback(current)
        current = random.choice(list(current.expand()))
        if current.value() > best.value():
            best = current
    return best


def exp_schedule(k=20, lam=0.05, limit=100):
    """One possible schedule function for simulated annealing"""
    return lambda t: (k * math.exp(-lam * t) if t < limit else 0)


def simulated_annealing(problem, schedule=exp_schedule(), callback=None):
    """[Fig. 4.5]
    If callback is not None, it must be a one-argument function that will be
    called at each step with the current node.
    """
    current = LSNode(problem, problem.initial, 0)
    best = current
    for t in range(sys.maxsize):
        if callback is not None:
            callback(current)
        T = schedule(t)
        if T == 0:
            return best
        next = random.choice(list(current.expand()))
        delta_e = next.value() - current.value()
        if delta_e > 0 or math.exp(delta_e / T) > random.uniform(0.0, 1.0):
            current = next
            if(current.value() > best.value()):
                best = current
        else:
            current = LSNode(problem, current.state, t + 1)



