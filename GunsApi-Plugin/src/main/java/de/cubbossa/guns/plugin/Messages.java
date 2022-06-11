package de.cubbossa.guns.plugin;

import de.cubbossa.translations.Message;
import de.cubbossa.translations.MessageFile;
import de.cubbossa.translations.MessageGroupMeta;
import de.cubbossa.translations.MessageMeta;

@MessageFile(
		author = "CubBossa",
		languageString = "en_US",
		version = "1.0",
		header = "")
public class Messages {

	@MessageMeta(value = "<#365b87>GunShed</#365b87> <gray>»</gray> ")
	public static final Message PREFIX = new Message("prefix");
	@MessageMeta(value = "<gold>Info</gold>")
	public static final Message INFO = new Message("info");

	@MessageMeta(value = "<gold>Previous Page</gold>")
	public static final Message GUI_PREV_PAGE_NAME = new Message("gui.general.prev_page.name");
	public static final Message GUI_PREV_PAGE_LORE = new Message("gui.general.prev_page.lore");
	@MessageMeta(value = "<gold>Next Page</gold>")
	public static final Message GUI_NEXT_PAGE_NAME = new Message("gui.general.next_page.name");
	public static final Message GUI_NEXT_PAGE_LORE = new Message("gui.general.next_page.lore");
	@MessageMeta(value = "<green>Accept</green>")
	public static final Message GUI_ACCEPT_NAME = new Message("gui.general.accept.name");
	public static final Message GUI_ACCEPT_LORE = new Message("gui.general.accept.lore");
	@MessageMeta(value = "<red>Decline</red>")
	public static final Message GUI_DECLINE_NAME = new Message("gui.general.decline.name");
	public static final Message GUI_DECLINE_LORE = new Message("gui.general.decline.lore");
	@MessageMeta(value = "<#F97C1B>Invalid</#F97C1B>")
	public static final Message GUI_WARNING_NAME = new Message("gui.general.warning.name");
	public static final Message GUI_WARNING_LORE = new Message("gui.general.warning.lore");
	@MessageMeta(value = "<gold>Back</gold>")
	public static final Message GUI_BACK_NAME = new Message("gui.general.back.name");
	public static final Message GUI_BACK_LORE = new Message("gui.general.back.lore");
	@MessageMeta(value = "<red>Error</red>")
	public static final Message GUI_ERROR_NAME = new Message("gui.general.error.name");
	public static final Message GUI_ERROR_LORE = new Message("gui.general.error.lore");


	@MessageMeta(value = "<message:prefix><gray> 'guns.nbo' successfully reloaded in <green><ms></green><dark_green>ms<dark_green><gray>.</gray>",
			placeholders = "ms")
	public static final Message RELOAD_SUCCESS = new Message("command.reload.success");
	@MessageMeta(value = """
			<red>An error occured while reloading 'guns.nbo':
			<red>------------------------------
			<red><error>
			<red>------------------------------""",
			placeholders = "error")
	public static final Message RELOAD_ERROR = new Message("command.reload.error");


	@MessageMeta(value = "Click a gun to equip")
	public static final Message G_GUNS_TITLE = new Message("gui.guns.title");
	@MessageMeta(value = "<message:info>")
	public static final Message G_GUNS_INFO_NAME = new Message("gui.guns.info.name");
	@MessageMeta(value = """
			<gray>»</gray> <yellow>left-click:</yellow> <gray>get gun</gray>
			<gray>»</gray> <yellow>right-click:</yellow> <gray>select gun<gray>
			""")
	public static final Message G_GUNS_INFO_LORE = new Message("gui.guns.info.lore");

	@MessageMeta(value = "<" + GunsAPI.COLOR_AMMO_HEX + ">Ammunition</" + GunsAPI.COLOR_AMMO_HEX + ">")
	public static final Message G_GUNS_AMMO_NAME = new Message("gui.guns.ammunition.name");
	@MessageMeta(value = """
			<gray>Without ammonition, your gun
			<gray>cannot shoot. Equip it with
			<gray>Ammunition or use existing Ammo
			<gray>from your inventory.
			""")
	public static final Message G_GUNS_AMMO_LORE = new Message("gui.guns.ammunition.lore");
	@MessageMeta(value = "<" + GunsAPI.COLOR_ATTACH_HEX + ">Attachments</" + GunsAPI.COLOR_ATTACH_HEX + ">")
	public static final Message G_GUNS_ATTACH_NAME = new Message("gui.guns.attachments.name");
	@MessageMeta(value = """
			<gray>Add attachments to your gun
			<gray>to modify its behaviour.
			""")
	public static final Message G_GUNS_ATTACH_LORE = new Message("gui.guns.attachments.lore");
	@MessageMeta(value = "<message:gui.guns.attachments.name>")
	public static final Message G_ATTACH_TITLE = new Message("gui.attachments.title");
	@MessageMeta(value = "<message:info>")
	public static final Message G_ATTACH_INFO_NAME = new Message("gui.attachments.info.name");
	@MessageMeta(value = "<message:gui.guns.attachments.name>")
	public static final Message G_ATTACH_INFO_LORE = new Message("gui.attachments.info.lore");
	@MessageMeta(value = "<message:gui.guns.ammunition.name>")
	public static final Message G_AMMO_TITLE = new Message("gui.ammunition.title");
	@MessageMeta(value = "<message:info>")
	public static final Message G_AMMO_INFO_NAME = new Message("gui.ammunition.info.name");
	@MessageMeta(value = "<message:gui.guns.ammunition.name>")
	public static final Message G_AMMO_INFO_LORE = new Message("gui.ammunition.info.lore");
	@MessageMeta(value = "Ammunition Count")
	public static final Message G_AMMO_COUNT_TITLE = new Message("gui.ammunition.count.title");
	@MessageMeta(value = "<" + GunsAPI.COLOR_BASIC_HEX + ">Ammunition Count</" + GunsAPI.COLOR_BASIC_HEX + ">")
	public static final Message G_AMMO_COUNT_NAME = new Message("gui.ammunition.count.name");
	@MessageMeta(value = """
			<gray>Current: </gray><white><amount></white>
			<gray>Set the ammunition count for
			<gray>your weapon
			""", placeholders = "amount")
	public static final Message G_AMMO_COUNT_LORE = new Message("gui.ammunition.count.lore");

	@MessageMeta(value = "<red>You must hold a gun in your hand to run this command.")
	public static final Message GUI_MUST_HOLD_GUN = new Message("command.must_hold_gun");

	@MessageGroupMeta(path = "error.parse", placeholders = "format")
	@MessageMeta(value = """
			<gray>A string of the following
			<gray>type is required:
			<aqua><format>""")
	public static final Message ERROR_PARSE_STRING = new Message("error.parse.string");
	@MessageMeta(value = "<gray>An integer number is required.")
	public static final Message ERROR_PARSE_INTEGER = new Message("error.parse.integer");
	@MessageMeta(value = "<gray>A float number is required.")
	public static final Message ERROR_PARSE_DOUBLE = new Message("error.parse.double");
	@MessageMeta(value = "<gray>A percentage is required:\n<aqua><format>")
	public static final Message ERROR_PARSE_PERCENT = new Message("error.parse.percent");
	@MessageMeta(value = """
			<gray>A date with the following
			<gray>format is required:
			<aqua><format>""")
	public static final Message ERROR_PARSE_DATE = new Message("error.parse.date");
	@MessageMeta(value = """
			<gray>A duration with the following
			<gray>format is required:
			<aqua><format>""")
	public static final Message ERROR_PARSE_DURATION = new Message("error.parse.duration");

	@MessageMeta(value = "Edit Your Gun Types")
	public static final Message E_GUNS_TITLE = new Message("gui.editor.guns.title");
	@MessageMeta(value = "<message:info>")
	public static final Message E_GUNS_INFO_NAME = new Message("gui.editor.guns.info.name");
	@MessageMeta(value = """
			<gray>»</gray> <yellow>left-click:</yellow> <gray>edit gun</gray>
			<gray>»</gray> <yellow>middle-click:</yellow> <gray>duplicate gun</gray>
			<gray>»</gray> <yellow>right-click:</yellow> <gray>delete gun<gray>
			""")
	public static final Message E_GUNS_INFO_LORE = new Message("gui.editor.guns.info.lore");
}
