import java.util.Scanner;

public class IpCheck {
	public static void main(String[] args) {
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.print("Enter IP1: ");
			String ip1 = scanner.nextLine();
			System.out.print("Enter MASK1: ");
			String mask1 = scanner.nextLine();

			System.out.print("Enter IP2: ");
			String ip2 = scanner.nextLine();
			System.out.print("Enter MASK2: ");
			String mask2 = scanner.nextLine();

			System.out.println("IP1: " + ip1 + ", MASK1: " + mask1);
			System.out.println("IP2: " + ip2 + ", MASK2: " + mask2);
			System.out.println("Can they connect? " + canConnect(ip1, mask1, ip2, mask2));
		}
	}

	// ドット区切りの文字列形式のIPアドレスを受け取り、そのIPアドレスを32ビットのlong値に変換
	public static long ipToLong(String ip) {
		String[] octets = ip.split("\\.");
		long result = 0;
		// 各オクテットを左にシフトして結果のlong変数にOR演算する
		for (int i = 0; i < 4; i++) {
			result |= Long.parseLong(octets[i]) << (24 - (8 * i));
		}
		return result;
	}

	// 32ビットのlong値のIPアドレスを受け取り、その値をドット区切りの文字列形式のIPアドレスに変換
	public static String longToIp(long ipLong) {
		String[] octets = new String[4];
		// 各オクテットを右にシフトして結果の文字列配列に代入
		for (int i = 0; i < 4; i++) {
			octets[3 - i] = String.valueOf((ipLong >> (8 * i)) & 0xFF);
		}
		return String.join(".", octets);
	}

	// 与えられたIPアドレスとサブネットマスクを使用して、ネットワークアドレスを計算
	public static String getNetworkAddress(String ip, String mask) {
		return longToIp(ipToLong(ip) & ipToLong(mask));
	}

	// 与えられたIPアドレスとサブネットマスクを使用して、ブロードキャストアドレスを計算
	public static String getBroadcastAddress(String ip, String mask) {
		return longToIp(ipToLong(ip) | (~ipToLong(mask) & ~ipToLong(mask) & 0xFFFFFFFFL));
	}

	// 指定されたIPアドレスが指定されたネットワークアドレスとブロードキャストアドレスの範囲内にあるかどうかを判定
	public static boolean isInRange(String ip, String networkAddress, String broadcastAddress) {
		long ipLong = ipToLong(ip);
		return ipLong >= ipToLong(networkAddress) && ipLong <= ipToLong(broadcastAddress);
	}

	// 2つのIPアドレス/サブネットマスクのペアを受け取り、それらが接続可能かどうかを判定。
	public static boolean canConnect(String ip1, String mask1, String ip2, String mask2) {
		return isInRange(ip1, getNetworkAddress(ip2, mask2), getBroadcastAddress(ip2, mask2)) ||
				isInRange(ip2, getNetworkAddress(ip1, mask1), getBroadcastAddress(ip1, mask1));
	}
}
