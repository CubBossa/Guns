package de.cubbossa.guns.plugin;

import de.cubbossa.translations.Message;
import de.cubbossa.translations.MessageFile;
import de.cubbossa.translations.MessageMeta;

@MessageFile(
        author = "CubBossa",
        languageString = "en_US",
        version = "1.0",
        header = "")
public class Messages {

    @MessageMeta(value = "<#365b87>GunShed</#365b87> <gray>»</gray> ")
    public static Message PREFIX = new Message("prefix");

    @MessageMeta(value = "<prefix><gray> 'guns.nbo' successfully reloaded in <green><ms></green><dark_green>ms<dark_green><gray>.</gray>",
            placeholders = "ms")
    public static Message RELOAD_SUCCESS = new Message("reload.success");
    @MessageMeta(value = """
            <red>An error occured while reloading 'guns.nbo':
            <red>------------------------------
            <red><error>
            <red>------------------------------""",
            placeholders = "error")
    public static Message RELOAD_ERROR = new Message("reload.error");

    @MessageMeta(value = "Click a gun to equip")
    public static Message GUNS_GUI_TITLE = new Message("gui.guns.title");
    @MessageMeta(value = "<red>You must hold a gun in your hand to run this command.")
    public static Message GUI_MUST_HOLD_GUN = new Message("gui.must_hold_gun");
}
