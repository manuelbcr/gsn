/**
* Global Sensor Networks (GSN) Source Code
* Copyright (c) 2006-2016, Ecole Polytechnique Federale de Lausanne (EPFL)
* 
* This file is part of GSN.
* 
* GSN is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* GSN is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with GSN.  If not, see <http://www.gnu.org/licenses/>.
* 
* File: src/ch/epfl/gsn/utils/graph/TestGraph.java
*
* @author Sofiane Sarni
*
*/

package ch.epfl.gsn.utils.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.epfl.gsn.utils.graph.Graph;
import ch.epfl.gsn.utils.graph.NodeNotExistsExeption;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;


public class TestGraph {
	
	@Test
	public void testRemoveCycles() throws NodeNotExistsExeption{
		Graph<String> graph = new Graph<String>();
		graph.addNode("n1");
		graph.addNode("n2");
		graph.addNode("n3");
		graph.addNode("n4");
		graph.addNode("n5");
		graph.addNode("n6");
		graph.addEdge("n2", "n3");
		graph.addEdge("n3", "n4");
		graph.addEdge("n1", "n5");
		graph.addEdge("n2", "n6");
		assertFalse(graph.hasCycle());
		graph.addEdge("n4", "n2");
		printGraph(graph, "testRemoveCycles1");
		assertTrue(graph.hasCycle());
		graph.removeNode("n4");
		assertNull(graph.findNode("n4"));
		assertNull(graph.findNode("n3"));
		assertNull(graph.findNode("n2"));
		printGraph(graph, "testRemoveCycles2");
		graph.addEdge("n6", "n5");
		graph.addEdge("n6", "n6");
		printGraph(graph, "testRemoveCycles3");
		assertTrue(graph.hasCycle());
		graph.removeNode("n6");
		assertNull(graph.findNode("n6"));
		assertNotNull(graph.findNode("n5"));
		printGraph(graph, "testRemoveCycles4");
	}
	
	@Test
	public void testRemoveNode() throws NodeNotExistsExeption{
		Graph<String> graph = new Graph<String>();
		graph.addNode("n1");
		graph.addNode("n2");
		graph.addNode("n3");
		graph.addNode("n4");
		graph.addNode("n5");
		graph.addNode("n6");
		graph.addEdge("n2", "n3");
		graph.addEdge("n3", "n4");
		graph.addEdge("n1", "n5");
		graph.addEdge("n2", "n6");
		printGraph(graph, "testRemoveNode1");
		graph.removeNode("n4");
		assertNull(graph.findNode("n2"));
		assertNotNull(graph.findNode("n6"));
		printGraph(graph, "testRemoveNode2");
	}
	
	@Test
	public void testFindRootNode() throws NodeNotExistsExeption{
		Graph<String> graph = new Graph<String>();
		graph.addNode("n1");
		graph.addNode("n2");
		graph.addNode("n3");
		graph.addNode("n4");
		graph.addNode("n5");
		graph.addNode("n6");
		graph.addEdge("n2", "n3");
		graph.addEdge("n3", "n4");
		graph.addEdge("n1", "n5");
		graph.addEdge("n2", "n6");
		assertEquals(graph.findRootNode(graph.findNode("n5")), graph.findNode("n1"));
		assertEquals(graph.findRootNode(graph.findNode("n1")), graph.findNode("n1"));
		assertNotSame(graph.findRootNode(graph.findNode("n1")), graph.findNode("n5"));
		assertEquals(graph.findRootNode(graph.findNode("n4")), graph.findNode("n2"));
	}

 @Test
    public void testGetNodes() throws NodeNotExistsExeption {
		Graph<String> graph = new Graph<String>();
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");

        List<String> expectedNodes = Arrays.asList("A", "B", "C");
        List<Node<String>> nodes = graph.getNodes();

        assertEquals(expectedNodes.size(), nodes.size());

        for (Node<String> node : nodes) {
            assertEquals(true, expectedNodes.contains(node.getObject()));
        }
    }

    @Test
    public void testGetRootNodes() throws NodeNotExistsExeption {
		Graph<String> graph = new Graph<String>();
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addEdge("A", "B");

        List<String> expectedRootNodes = Arrays.asList("A", "C");
        List<Node<String>> rootNodes = graph.getRootNodes();

        assertEquals(expectedRootNodes.size(), rootNodes.size());

        for (Node<String> node : rootNodes) {
            assertEquals(true, expectedRootNodes.contains(node.getObject()));
        }
    }



	@Test
    public void testGetNodesByDFSSearch() throws NodeNotExistsExeption {
		Graph<String> graph = new Graph<String>();
		graph.addNode("A");
		graph.addNode("B");
		graph.addNode("C");
		graph.addNode("D");
		graph.addEdge("A", "B");
		graph.addEdge("B", "C");
		graph.addEdge("A", "D");
		graph.addEdge("D", "C");
		

		List<String> result = graph.getNodesByDFSSearch();
		List<String> expected = Arrays.asList("C", "B", "A");

		assertTrue(result.containsAll(expected));
    	assertTrue(expected.containsAll(result));
    }

	 @Test
    public void testGetDescendingNodes() throws NodeNotExistsExeption {
        Graph<String> graph = new Graph<String>();
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");

        Node<String> testNode = graph.findNode("A");

        List<Node<String>> result = graph.getDescendingNodes(testNode);
        List<Node<String>> expected = Arrays.asList(
            graph.findNode("C"),
            graph.findNode("B"),
            graph.findNode("A")
        );

        assertEquals(expected, result);
    }


	@Test
    public void testAddEdge() throws EdgeExistsException {
        Node<String> nodeA = new Node<>("A");
        Node<String> nodeB = new Node<>("B");

        Edge<String> edgeAB = nodeA.addEdge(nodeB);

        assertTrue(nodeA.getOutputEdges().contains(edgeAB));
        assertTrue(nodeB.getInputEdges().contains(edgeAB));
    }

    @Test
    public void testRemoveEdge() throws EdgeExistsException {
        Node<String> nodeA = new Node<>("A");
        Node<String> nodeB = new Node<>("B");

        Edge<String> edgeAB = nodeA.addEdge(nodeB);
        boolean removed = nodeA.removeEdge(nodeB);

        assertTrue(removed);
        assertFalse(nodeA.getOutputEdges().contains(edgeAB));
        assertFalse(nodeB.getInputEdges().contains(edgeAB));
    }

    @Test
    public void testEquals() {
        Node<String> nodeA = new Node<>("A");
        Node<String> nodeA2 = new Node<>("A");
        Node<String> nodeB = new Node<>("B");

        assertTrue(nodeA.equals(nodeA2));
        assertFalse(nodeA.equals(nodeB));
    }

    @Test
    public void testToString() {
        Node<String> nodeA = new Node<>("A");

        assertEquals("Node[A]", nodeA.toString());
    }

	@Test
    public void testNodeMethods() {
        Node<String> node = new Node<>("A");

        ArrayList<Edge<String>> inputEdges = new ArrayList<>();
        inputEdges.add(new Edge<>(new Node<>("B"), node));
        node.setInputEdges(inputEdges);
        assertEquals(inputEdges, node.getInputEdges());

        
        ArrayList<Edge<String>> outputEdges = new ArrayList<>();
        outputEdges.add(new Edge<>(node, new Node<>("C")));
        node.setOutputEdges(outputEdges);
        assertEquals(outputEdges, node.getOutputEdges());

        
        node.setObject("D");
        assertEquals("D", node.getObject());

        node.setRoot(true);
        assertTrue(node.isRoot());

        node.setRemoved(true);
        assertTrue(node.isRemoved());
    }


	private void printGraph(Graph graph, String message) {
		System.out.println("===================" + message + "==================");
		System.out.println(graph);
	}
}
