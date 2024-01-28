package com.egor4.minesweeper.commands;

import com.egor4.minesweeper.external.Board;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.TntEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.Objects;

public class MinesweeperCommand {

    private static final TextColor[] a = new TextColor[9];

    public static Board board;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment){
        dispatcher.register(CommandManager.literal("minesweeper")
                .then(CommandManager.literal("start").executes(MinesweeperCommand::start))
        );
        dispatcher.register(CommandManager.literal("minesweeper").
                then(CommandManager.literal("open").
                        then(CommandManager.argument("xVar", IntegerArgumentType.integer()).
                                then(CommandManager.argument("yVar", IntegerArgumentType.integer()).executes(MinesweeperCommand::open)
                                )
                        )
                )
        );
        dispatcher.register(CommandManager.literal("minesweeper").then(CommandManager.literal("flagmode").executes(context -> { board.flagMode = !board.flagMode; return 1;})));
        a[0] = TextColor.fromRgb(0x0000ff);
        a[1] = TextColor.fromRgb(0x00ff00);
        a[2] = TextColor.fromRgb(0xff0000);
        a[3] = TextColor.fromRgb(0xaa00aa);
        a[4] = TextColor.fromRgb(0xaaaa00);
        a[5] = TextColor.fromRgb(0x00aaaa);
        a[6] = TextColor.fromRgb(0xa0a0a0);
        a[7] = TextColor.fromRgb(0x0a0a0a);
        a[8] = TextColor.fromRgb(0xbbbbbb);
    }

    public static int start(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        board = new Board();
        display(context);
        return 1;
    }

    public static int open(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if(!board.getGameState()){
            board.uncover(IntegerArgumentType.getInteger(context, "yVar"), IntegerArgumentType.getInteger(context, "xVar"));
            if(!board.getGameState())
            return display(context);
        }
        context.getSource().getPlayer().sendMessage(Text.of("You lost"));
        ServerWorld world = context.getSource().getWorld();
        Vec3d player = context.getSource().getPlayer().getPos();
        TntEntity tnt = new TntEntity((World) world, player.x, player.y, player.z, null);
        world.createExplosion(tnt, tnt.getX(), tnt.getY(), tnt.getZ(), 100.f, Explosion.DestructionType.BREAK);
        return 1;
    }

    public static int display(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MutableText str = MutableText.of(new LiteralTextContent(""));
        MutableText current;
        for(int i = 0; i < board.height; i++){
            for(int j = 0; j < board.width;j++) {
                if (board.board[i][j]) {
                    if(board.values[i][j] == -5) {
                        current = MutableText.of(new LiteralTextContent("♂"));
                        current.setStyle(current.getStyle().withColor(0xf0f00f));
                    }
                    else{
                        current = MutableText.of(new LiteralTextContent(String.valueOf(board.values[i][j])));
                        current.setStyle(current.getStyle().withColor(a[board.values[i][j]]));
                    }
                } else {
                    current = MutableText.of(new LiteralTextContent("X"));
                    current.setStyle(current.getStyle().withColor(0x888888));
                    current.setStyle(current.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/minesweeper open %d %d", j, i))));
                }
                str.append(current);
            }
            str.append("\n");
        }
        current = MutableText.of(new LiteralTextContent("SetFlag"));
        current.setStyle(current.getStyle().withColor(board.flagMode ? 0x009900 : 0x990000).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/minesweeper flagmode")));
        str.append(current);
        Objects.requireNonNull(context.getSource().getPlayer()).sendMessage(str);
        return 1;
    }
}



