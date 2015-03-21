package us.mcmagic.magicassistant.show.actions;

import org.bukkit.ChatColor;
import us.mcmagic.magicassistant.show.Show;
import us.mcmagic.mcmagiccore.title.TitleObject;

/**
 * Created by Marc on 1/10/15
 */
public class TitleAction extends ShowAction {
    public TitleObject.TitleType type;
    public String title;
    public int fadeIn;
    public int fadeOut;
    public int stay;

    public TitleAction(Show show, long time, TitleObject.TitleType type, String title, int fadeIn, int fadeOut, int stay) {
        super(show, time);
        this.type = type;
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
        this.stay = stay;
    }

    @Override
    public void play() {
        Show.displayTitle(new TitleObject(title, type).setFadeIn(fadeIn).setFadeOut(fadeOut).setStay(stay));
    }
}
