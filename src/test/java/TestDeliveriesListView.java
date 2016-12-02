import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import model.Checkpoint;
import model.Waypoint;
import view.DeliveriesListView;

/**
 * Teste les méthodes de la classe DeliveriesListView
 */
public class TestDeliveriesListView {
	
	@Test
	public void testDurationDisplay() {
		Checkpoint c1 = new Checkpoint(new Waypoint(1, 0, 0), 300, new Date(), new Date()); // 00h05
		Checkpoint c2 = new Checkpoint(new Waypoint(2, 0, 0), 600, new Date(), new Date()); // 00h10
		Checkpoint c3 = new Checkpoint(new Waypoint(3, 0, 0), 3600, new Date(), new Date()); // 01h00
		Checkpoint c4 = new Checkpoint(new Waypoint(4, 0, 0), 36060, new Date(), new Date()); // 10h01

		String str1 = DeliveriesListView.deliveryToText(c1);
		String str2 = DeliveriesListView.deliveryToText(c2);
		String str3 = DeliveriesListView.deliveryToText(c3);
		String str4 = DeliveriesListView.deliveryToText(c4);

		assertEquals(str1, "Adresse : 1\nDurée : 00h05");
		assertEquals(str2, "Adresse : 2\nDurée : 00h10");
		assertEquals(str3, "Adresse : 3\nDurée : 01h00");
		assertEquals(str4, "Adresse : 4\nDurée : 10h01");
	}
}
