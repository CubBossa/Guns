package de.cubbossa.guns.api.attachments;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.NamespacedKey;

@Getter
@RequiredArgsConstructor
public abstract class LogicAttachment implements Attachment {

	private final NamespacedKey key;

}
