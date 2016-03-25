package pm;

public class Main {

	public static void main(String[] args) {
		if (args.length > 0) {
			try {
				int s = Integer.parseInt(args[0]);
				Builder b = new Builder(s, "2016");
			} catch (NumberFormatException e) {
				System.out.println("Meeting numbers can only be digits. Please obey");
			}
			
		}
	}
}
