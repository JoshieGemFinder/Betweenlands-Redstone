package com.joshiegemfinder.betweenlandsredstone.util;

public class ScabystColor {
	
	public static class RGBColor {

        private int r;
        private int g;
        private int b;

        public RGBColor(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public RGBColor(double r, double g, double b) {
            this.r = (int)Math.floor(r);
            this.g = (int)Math.floor(r);
            this.b = (int)Math.floor(r);
        }

        public int getR() {
            return r;
        }

        public int getG() {
            return g;
        }

        public int getB() {
            return b;
        }
    }
	
	///The color the dust will show up at different redstone power levels
	//oldShades were taken from the colouredredstone mods
    public static final RGBColor[] oldShades = {
    		new RGBColor(28, 118, 147/*32, 135, 168*/), new RGBColor(41, 140, 172), new RGBColor(47, 143, 176), new RGBColor(55, 149, 180),
            new RGBColor(61, 153, 184), new RGBColor(67, 157, 188), new RGBColor(73, 162, 193), new RGBColor(79, 167, 196),
            new RGBColor(85, 172, 201), new RGBColor(90, 176, 205), new RGBColor(96, 180, 209), new RGBColor(101, 185, 214),
            new RGBColor(106, 190, 217), new RGBColor(112, 194, 222), new RGBColor(117, 200, 226), new RGBColor(122, 204, 230)
        };
    
	public static final RGBColor[] shades = {
			new RGBColor(26, 78, 68), new RGBColor(59, 108, 99), new RGBColor(71, 119, 110), new RGBColor(83, 131, 121), new RGBColor(89, 137, 127), new RGBColor(96, 143, 133), new RGBColor(109, 154, 145), new RGBColor(121, 166, 157), new RGBColor(127, 172, 162), new RGBColor(134, 178, 168), new RGBColor(146, 188, 180), new RGBColor(158, 199, 191), new RGBColor(162, 204, 196), new RGBColor(167, 209, 201), new RGBColor(177, 217, 210), new RGBColor(185, 224, 216)
	};	
    
}
