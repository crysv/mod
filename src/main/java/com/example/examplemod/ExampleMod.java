package com.example.examplemod;

import com.mojang.authlib.yggdrasil.request.JoinMinecraftServerRequest;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Function15;
import com.mojang.logging.LogUtils;

import imgui.ImGui;
import imgui.app.Application;
import imgui.app.Configuration;

import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraft.world.level.storage.PrimaryLevelData;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SimpleOptionsSubScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPingPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.ServerStatus.Players;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry.LoginPayload;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.MixinEnvironment.Side;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.stream.Collectors;



// The value here should match an entry in the META-INF/mods.toml file
@Mod("examplemod")
public class ExampleMod
{
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    private Minecraft mc;

    private KeyMapping keyZ;
    private KeyMapping keyX;
    private KeyMapping keyF;
    private KeyMapping keyR;
    private KeyMapping keyC;

    private Ai ai;

    public ExampleMod()
    {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        mc = Minecraft.getInstance();

        keyZ = new KeyMapping("key z", InputConstants.KEY_Z, KeyMapping.CATEGORY_GAMEPLAY);
        keyX = new KeyMapping("key x", InputConstants.KEY_X, KeyMapping.CATEGORY_GAMEPLAY);
        keyF = new KeyMapping("key f", InputConstants.KEY_F, KeyMapping.CATEGORY_GAMEPLAY);
        keyR = new KeyMapping("key r", InputConstants.KEY_R, KeyMapping.CATEGORY_GAMEPLAY);
        keyC = new KeyMapping("key c", InputConstants.KEY_C, KeyMapping.CATEGORY_GAMEPLAY);

        ai = new Ai();

        mc.options.gamma = 10000;
    }

    static double change(double value, int decimalpoint)
    {
        // Using the pow() method
        value = value * Math.pow(10, decimalpoint);
        value = Math.floor(value);
        value = value / Math.pow(10, decimalpoint);
    
        return value;
    }

    int startx;
    int starty;
    int endx;
    int endy;

    double yJump = 6;

    @SubscribeEvent
    public void tick(PlayerTickEvent event) 
    {
        if(event.side == LogicalSide.CLIENT)
        {
            LocalPlayer p = mc.player;
            if (p != null) {
                if (event.phase == Phase.START)
                {   

                }
                if (event.phase == Phase.END)
                {
                    p.setNoGravity(false);
                    p.setOnGround(true);
                    Vec3 v = p.position();
                    long x = ((long)(v.x() * 1000)) % 10;
                    long z = ((long)(v.z() * 1000)) % 10;
                    if (x!=0&&z!=0) 
                    {
                        double dz;
                        double dx;
                        if (x < 5)
                            dx = change(v.x,2);
                        else dx = change(v.x+0.01, 2);
                        if (z < 5)
                            dz = change(v.z,2);
                        else dz = change(v.z+0.01,2);
                        //long rx = ((long)(dx * 1000)) % 10;
                        //long rz = ((long)(dz * 1000)) % 10;
                        //LOGGER.info("x: {} z: {}",rx,rz);
                        p.setPos(new Vec3(dx,v.y,dz));
                        if (p.getVehicle()!=null) p.getVehicle().setPos(dx,v.y,dz);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Post event)
    {
        
    } 

    @SubscribeEvent
    public void render(RenderGameOverlayEvent event)
    {
        if(mc.font != null)
            GuiComponent.drawString(event.getMatrixStack(),mc.font,String.format("%f",yJump),0,0,0x404040);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // Some example code to dispatch IMC to another mod
        InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // Some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream()
                .map(m->m.messageSupplier().get())
                .collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents
    {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent)
        {
            // Register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }
}
