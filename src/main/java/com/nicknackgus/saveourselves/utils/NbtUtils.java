package com.nicknackgus.saveourselves.utils;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Optional;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType.NbtPath;
import net.minecraft.nbt.NbtElement;

public class NbtUtils {

	public static Optional<NbtPath> getNbtPath(String path) {
		try {
			return Optional.of(new NbtPathArgumentType().parse(new StringReader(path)));
		} catch (CommandSyntaxException e) {
			return Optional.empty();
		}
	}

	public static List<NbtElement> getNbtAtPath(NbtElement root, NbtPath nbtPath) {
		if (root == null || nbtPath == null) {
			return List.of();
		}
		try {
			return nbtPath.get(root);
		} catch (CommandSyntaxException e) {
			return List.of();
		}
	}

}
