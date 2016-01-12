package architecture;


public class FourLutSanitizedDisjoint extends FourLutSanitizedAbstract {
	public FourLutSanitizedDisjoint(int width, int height, int channelWidth, int K, int L) {
		super(width,height,channelWidth,K,L);
	}

	@Override
	protected void generateSwitchBlocks() {
		for (int x= 1; x<width+1; x++) {
			for (int y= 0; y<height+1; y++) {
				if (x!=1) RouteNode.connect(horizontalChannels[x][y],horizontalChannels[x-1][y]);
				if (x!=width)RouteNode.connect(horizontalChannels[x][y],horizontalChannels[x+1][y]);
				if (y!=0){
					RouteNode.connect(horizontalChannels[x][y],verticalChannels[x][y]);
					RouteNode.connect(horizontalChannels[x][y],verticalChannels[x-1][y]);
				}
				if (y!=height) {
					RouteNode.connect(horizontalChannels[x][y],verticalChannels[x][y+1]);
					RouteNode.connect(horizontalChannels[x][y],verticalChannels[x-1][y+1]);
				}
			}
		}
		
		for (int x= 0; x<width+1; x++) {
			for (int y= 1; y<height+1; y++) {
				if (y!=1) RouteNode.connect(verticalChannels[x][y],verticalChannels[x][y-1]);
				if (y!=height)RouteNode.connect(verticalChannels[x][y],verticalChannels[x][y+1]);
				if (x!=0){
					RouteNode.connect(verticalChannels[x][y],horizontalChannels[x][y]);
					RouteNode.connect(verticalChannels[x][y],horizontalChannels[x][y-1]);
				}
				if (x!=width) {
					RouteNode.connect(verticalChannels[x][y],horizontalChannels[x+1][y]);
					RouteNode.connect(verticalChannels[x][y],horizontalChannels[x+1][y-1]);
				}
			}
		}
	}

}
