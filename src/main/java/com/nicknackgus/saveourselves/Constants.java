package com.nicknackgus.saveourselves;

public class Constants {
	public enum Note {
		FS3(0, "F♯₃"),
		GB3(0, "F♭₃"),
		G3(1, "G₃"),
		GS3(2, "G♯₃"),
		AB3(2, "A♭₃"),
		A3(3, "A₃"),
		AS3(4, "A♯₃"),
		BB3(4, "B♭₃"),
		B3(5, "B₃"),
		C4(6, "C₄"),
		CS4(7, "C♯₄"),
		DB4(7, "D♭₄"),
		D4(8, "D₄"),
		DS4(9, "D♯₄"),
		EB4(9, "E♭₄"),
		E4(10, "E₄"),
		F4(11, "F₄"),
		FS4(12, "F♯₄"),
		GB4(12, "F♭₄"),
		G4(13, "G₄"),
		GS4(14, "G♯₄"),
		AB4(14, "A♭₄"),
		A4(15, "A₄"),
		AS4(16, "A♯₄"),
		BB4(16, "B♭₄"),
		B4(17, "B₄"),
		C5(18, "C₅"),
		CS5(19, "C♯₅"),
		DB5(19, "D♭₅"),
		D5(20, "D₅"),
		DS5(21, "D♯₅"),
		EB5(21, "E♭₅"),
		E5(22, "E₅"),
		F5(23, "F₅"),
		FS5(24, "F♯₅"),
		GB5(24, "F♭₅");

		public final int index;
		public final String name;
		public final float pitch;

		Note(int index, String name) {
			this.index = index;
			this.name = name;
			this.pitch = (float)Math.pow(2.0, (double)(index - 12) / 12.0);
		}
	}
}
