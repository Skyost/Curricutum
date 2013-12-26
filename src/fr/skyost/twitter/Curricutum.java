package fr.skyost.twitter;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import fr.skyost.twitter.utils.Utils;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class Curricutum {
	
	private static final String VERSION = "0.1";
	private static final Properties PROPERTIES = new Properties();
	
	private static AccessToken accessToken;
	
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		final File propertiesFile = new File("Curricutum.xml");
		final Twitter twitter = TwitterFactory.getSingleton();
		twitter.setOAuthConsumer("RijoX9ZXTv1TPnbNJ1QqLg", "asSiSHW9T2owMhX4z1wuYb2GTp7MKislo1LPMMCOBKE"); // I know :P
		if(!propertiesFile.exists()) {
			final RequestToken requestToken = twitter.getOAuthRequestToken();
			if(Desktop.isDesktopSupported()) {
				Desktop.getDesktop().browse(new URI(requestToken.getAuthorizationURL()));
			}
			else {
				JOptionPane.showMessageDialog(null, "You must have a desktop to run this application.", "Curricutum", JOptionPane.ERROR_MESSAGE);
			}
			final String pin = (String)JOptionPane.showInputDialog(null, "<html>A page has been opened on your default browser.<br>Please authorize the application and enter the PIN (if available) or just hit enter :</html>", "Curricutum", JOptionPane.PLAIN_MESSAGE, new ImageIcon(Curricutum.class.getResource("/fr/skyost/twitter/res/CV.png")), null, null);
			if(pin != null) {
				try {
					if(pin.length() > 0) {
						accessToken = twitter.getOAuthAccessToken(requestToken, pin);
					}
					else {
						accessToken = twitter.getOAuthAccessToken();
					}
					PROPERTIES.put("token", accessToken.getToken());
					PROPERTIES.put("token-secret", accessToken.getTokenSecret());
					PROPERTIES.put("user-id", String.valueOf(accessToken.getUserId()));
					PROPERTIES.storeToXML(new FileOutputStream(propertiesFile), "Curricutum by Skyost");
				}
				catch(TwitterException ex) {
					if(ex.getStatusCode() == 401) {
						JOptionPane.showMessageDialog(null, "Unable to get access token !", "Curricutum", JOptionPane.ERROR_MESSAGE);
					}
					else {
						JOptionPane.showMessageDialog(null, "Error '" + ex.getMessage() + "'.", "Curricutum", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			else {
				System.exit(0);
			}
		}
		else {
			PROPERTIES.loadFromXML(new FileInputStream(propertiesFile));
			accessToken = new AccessToken(PROPERTIES.getProperty("token", "0"), PROPERTIES.getProperty("token-secret", "0"), Long.parseLong(PROPERTIES.getProperty("user-id", "0")));
			twitter.setOAuthAccessToken(accessToken);
		}
		final User user = twitter.showUser(twitter.getId());
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		fileChooser.setDialogTitle("Export your CV...");
		fileChooser.setFileFilter(new FileNameExtensionFilter("Html file (*.html)", "html"));
		fileChooser.showOpenDialog(null);
		final File selected = new File(fileChooser.getSelectedFile() + ".html");
		final File cssFile = new File(selected.getParentFile(), "base.css");
		if(!cssFile.exists()) {
			Utils.extractFromJAR("/fr/skyost/twitter/res/base.css", cssFile);
			Utils.extractFromJAR("/fr/skyost/twitter/res/model.css", new File(cssFile.getParentFile(), "model.css"));
		}
		final PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(selected), "UTF-8"));
		writer.println("<!-- Generated by Curricutum v" + VERSION + " by Skyost -->");
		writer.println("<html>");
		writer.println("<head>");
		writer.println("<title>" + user.getName() + "</title>");
		writer.println("<meta content=\"text/html; charset=utf-8\" http-equiv=\"content-type\">");
		writer.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"base.css\" media=\"all\"/>");
		writer.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"model.css\" media=\"all\"/>");
		writer.println("<body>");
		writer.println("<div id=\"global\">");
		writer.println("<div id=\"entete\">");
		writer.println("<h1><img src=\"" + user.getProfileImageURL() + "\" alt=\"Me\" border=\"0\"> " + user.getName() + "</h1>");
		writer.println("<h2>@" + user.getScreenName() + "</h2>");
		writer.println("</div>");
		writer.println("</div>");
		writer.println("<div id=\"contenu\">");
		writer.println("<b>Me IRL :</b>");
		writer.println("<br>I think I am " + user.getDescription() + " I speak " + user.getLang().toUpperCase() + " because I am based at " + user.getLocation() + ".");
		writer.println("<br>My favourite color is <font color=\"#" + user.getProfileLinkColor() + "\">this</font> and my second favourite color is <font color=\"#" + user.getProfileTextColor() + "\">this</font>.");
		writer.println("<br>The last thing I have said is : <i>\"" + user.getStatus().getText() + "\"</i>.");
		writer.println("<br>You can view my website <a href=\"http://" + user.getURL() + "\">here</a>.");
		writer.println("<br><br><b>Me on Twitter :</b>");
		writer.println("<br>I have actually " + user.getFollowersCount() + " followers, " + user.getFavouritesCount() + " favourites tweets and " + user.getFriendsCount() + " friends.");
		writer.println("<br>I have " + user.getStatusesCount() + " tweets.");
		writer.println("<br><br><b>This is all my Twitter account says about me.");
		writer.println("<br>If you want more details, feel free to send me a tweet !");
		writer.println("</div>");
		writer.println("<div id=\"pied\">");
		writer.println("Generated by Curricutum.");
		writer.println("</div>");
		writer.println("</body>");
		writer.println("</html>");
		writer.flush();
		writer.close();
	}

}