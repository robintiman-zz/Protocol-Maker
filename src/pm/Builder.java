package pm;

import java.io.File;

import org.omg.Messaging.SyncScopeHelper;

public class Builder {
	private File file;
	private int meeting;

	public Builder() {
		file = new File("/home/robintiman/Documents/Propaganda/Mötesanteckningar/protkoll_S" + meeting);
	}
}
