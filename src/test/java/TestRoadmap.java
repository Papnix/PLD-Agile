import static org.junit.Assert.*;

import org.junit.Test;

import controller.Roadmap;
import model.Section;
import model.Waypoint;

/**
 * Tests the Roadmap's methods
 */
public class TestRoadmap {
	
	@Test
	public void testDescriptionForBend() {
		// Waypoints correspondants à ceux du fichier "plan5x5.xml", sauf le 8
		Waypoint w0 = new Waypoint(0, 134, 193);
		Waypoint w1 = new Waypoint(1, 195, 291);
		Waypoint w2 = new Waypoint(2, 140, 420);
		Waypoint w6 = new Waypoint(6, 244, 277);
		Waypoint w7 = new Waypoint(7, 244, 345);
		Waypoint w8 = new Waypoint(7, 244, 404);
		Waypoint w11 = new Waypoint(11, 378, 209);
		Waypoint w12 = new Waypoint(12, 433, 455);
		Waypoint w16 = new Waypoint(16, 488, 320);
		Waypoint w21 = new Waypoint(21, 651, 255);
		Waypoint w22 = new Waypoint(22, 619, 355);

		Section s0_1 = new Section("s0_1", 0, 0, false, w0, w1);
		Section s1_6 = new Section("s1_6", 0, 0, false, w1, w6);
		Section s1_2 = new Section("s1_2", 0, 0, false, w1, w2);
		Section s2_7 = new Section("s2_7", 0, 0, false, w2, w7);
		Section s7_6 = new Section("s7_6", 0, 0, false, w7, w6);
		Section s7_12 = new Section("s7_12", 0, 0, false, w7, w12);
		Section s16_11 = new Section("s16_11", 0, 0, false, w16, w11);
		Section s11_12 = new Section("s11_12", 0, 0, false, w11, w12);
		Section s16_21 = new Section("s16_21", 0, 0, false, w16, w21);
		Section s21_22 = new Section("s21_22", 0, 0, false, w21, w22);
		Section s6_7 = new Section("s6_7", 0, 0, false, w6, w7);
		Section s7_8 = new Section("s7_8", 0, 0, false, w7, w8);

		assertEquals("Tourner à gauche", Roadmap.getDescriptionForBend(s0_1, s1_6));
		assertEquals("Tourner à droite", Roadmap.getDescriptionForBend(s0_1, s1_2));
		assertEquals("Tourner à gauche", Roadmap.getDescriptionForBend(s2_7, s7_6));
		assertEquals("Tourner à droite", Roadmap.getDescriptionForBend(s2_7, s7_12));
		assertEquals("Tourner à gauche", Roadmap.getDescriptionForBend(s16_11, s11_12));
		assertEquals("Tourner à droite", Roadmap.getDescriptionForBend(s16_21, s21_22));
		assertEquals("Faire demi-tour", Roadmap.getDescriptionForBend(s6_7, s7_6));
		assertEquals("Continuer tout droit", Roadmap.getDescriptionForBend(s6_7, s7_8));
	}
}
