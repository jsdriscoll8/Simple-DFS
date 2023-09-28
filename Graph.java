// jdh CS3240A / CS 5990A Fall 2023

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;

public class Graph {
  List<Node> nodes;

  public Graph() {
    this.nodes = new ArrayList<Node>();
  }

  public void addNode(Node newNode) {
    for (Node n: this.nodes) {
      if (n == newNode) {
        System.out.println("ERROR: graph already has a node " + n.name);
        assert false;
      }
    }
    nodes.add(newNode);
  }

  public void addEdge(Node n1, Node n2) {
    // make sure edge does not already exist
    int idx1 = this.nodes.indexOf(n1);
    if (idx1 >= 0) {
      for (Node adjnode: this.nodes.get(idx1).adjlist) {
        if (adjnode == n2) {
          System.out.println("ERROR: there is already an edge from " + n1.name + " to " + n2.name);
          return;
        }
      }
      this.nodes.get(idx1).addEdge(n2);
    } else {
      System.out.println("ERROR: node " + n1.name + " not found in graph");
    }

    int idx2 = this.nodes.indexOf(n2);
    if (idx2 >= 0) {
      this.nodes.get(idx2).addEdge(n1);
    } else {
      System.out.println("ERROR: node " + n2.name + " not found in graph");
    }
  } // addEdge()

  //----------------------------------------------------------------

  public void print() {
    for (Node n1: this.nodes) {
      System.out.print(n1 + ":");
      for (Node n2: n1.adjlist) {
        System.out.print(" " + n2);
      }
      System.out.print("|");
    }
    System.out.println();
  } // print()

  //----------------------------------------------------------------

  public List<Node> DFS(Node s) {
    // Initialize the shortest cycle List
    List<Node> smallestCycle = new ArrayList<Node>();

    // Initialize the stack and add s to it.
    Stack<Node> stack = new Stack<Node>();
    stack.push(s);

    // Initialize the map of explored nodes, add each node with bool value false
    Map<Node, Boolean> explored = new HashMap<Node, Boolean>();
    for(Node n : nodes)
      explored.put(n, false);

    // Initialize the current node
    Node curNode;

    // Initialize the minimum size found
    int minSizeFound = Integer.MAX_VALUE;

    while(!stack.empty()) {
      // Set the current node
      curNode = stack.pop();

      // If all nodes are explored, break.
      if (!explored.get(curNode)) {
        explored.replace(curNode, true);

        // Discover every node in the adjacency list
        for (Node adjNode : curNode.adjlist) {

          // If adjacency list node is unexplored
          if (!explored.get(adjNode)) {

            // If it has a parent - must have been discovered
            if (adjNode.parent != null) {
              // Start cycle checking. Cycle has not been found, not at top adj/cur layers minimum size is 3
              boolean cycleFound = false;
              boolean reachedTopAdj = false, reachedTopCur = false;

              // Clear cycle list.
              List<Node> curCycle = new ArrayList<Node>();
              Node adjCycleNode = adjNode;
              Node curCycleNode = curNode;

              // Add the adjacent node, its parent, and the current node to the cycle (minimum)
              curCycle.add(curCycleNode);
              curCycle.add(adjCycleNode);

              while(!cycleFound && curCycle.size() <= minSizeFound){
                // Check: are the parents the same?
                if(!reachedTopAdj)
                  curCycle.add(adjCycleNode.parent);

                if(curCycleNode.parent.equals(adjCycleNode.parent)) {
                  cycleFound = true;
                  minSizeFound = curCycle.size();
                  smallestCycle = curCycle;
                }

                // Check: are the parents adjacent / on the same layer?
                if(!cycleFound) {
                  // Add current node parent to cycle list
                  if(!reachedTopCur)
                    curCycle.add(curCycleNode.parent);

                  for (int j = 0; j < adjCycleNode.parent.adjlist.size(); j++) {
                    if(adjCycleNode.parent.adjlist.get(j).equals(curCycleNode.parent)){
                      cycleFound = true;
                    }
                  }
                }

                // Cycle found, set smallest size cycle & set return value.
                if(cycleFound) {
                  minSizeFound = curCycle.size();
                  smallestCycle = curCycle;
                }

                // Cycle not found. Append the size, set cycle nodes to their current parents.
                else {
                  if(adjCycleNode.parent.parent != null) {
                    adjCycleNode = adjCycleNode.parent;
                  }
                  else {
                    reachedTopAdj = true;
                  }
                  if(curCycleNode.parent.parent != null) {
                    curCycleNode = curCycleNode.parent;
                  }
                  else {
                    reachedTopCur = true;
                  }
                }
              }
            }
            // Set the parent of the adjacent node to the current node
            adjNode.parent = curNode;

            // Add the adjacent node to the stack
            stack.push(adjNode);
          }
        }
      }
    }
    return smallestCycle;
  } // DFS()

} // class Graph
