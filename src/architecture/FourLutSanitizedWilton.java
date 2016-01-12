package architecture;


public class FourLutSanitizedWilton extends FourLutSanitizedAbstract {
	public FourLutSanitizedWilton(int width, int height, int channelWidth, int K, int L) {
		super(width, height, channelWidth, K, L);
	}

	@Override
	protected void generateSwitchBlocks() {
		//Generating switch blocks, connect routing wires (Wilton switch block)
		//Install the connection possibilities from the horizontal channels
		for (int x= 1; x<width+1; x++) {
			for (int y= 0; y<height+1; y++) {
				if (x!=1) RouteNode.connect(horizontalChannels[x][y],horizontalChannels[x-1][y]);
				if (x!=width)RouteNode.connect(horizontalChannels[x][y],horizontalChannels[x+1][y]);
				if (y!=0){
					RouteNode.connect(horizontalChannels[x][y],verticalChannels[x][y],1,-1);
					RouteNode.connect(horizontalChannels[x][y],verticalChannels[x-1][y],-1,-2);
				}
				if (y!=height) {
					RouteNode.connect(horizontalChannels[x][y],verticalChannels[x][y+1],0,-1);
					RouteNode.connect(horizontalChannels[x][y],verticalChannels[x-1][y+1],1,-1);
				}
			}
		}
		//Connect vertical channels
		//Install the connection possibilities from the vertical channels
		for (int x= 0; x<width+1; x++) {
			for (int y= 1; y<height+1; y++) {
				if (y!=1) RouteNode.connect(verticalChannels[x][y],verticalChannels[x][y-1]);
				if (y!=height)RouteNode.connect(verticalChannels[x][y],verticalChannels[x][y+1]);
				if (x!=0){
					RouteNode.connect(verticalChannels[x][y],horizontalChannels[x][y],1,1);
					RouteNode.connect(verticalChannels[x][y],horizontalChannels[x][y-1],-1,0);
				}
				if (x!=width) {
					RouteNode.connect(verticalChannels[x][y],horizontalChannels[x+1][y],-1,-2);
					RouteNode.connect(verticalChannels[x][y],horizontalChannels[x+1][y-1],1,1);
				}
			}
		}
	}

}
