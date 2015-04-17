package us.mcmagic.magicassistant.ridemanager;

import net.minecraft.server.v1_8_R2.BlockMinecartTrackAbstract;
import net.minecraft.server.v1_8_R2.EntityMinecartRideable;
import net.minecraft.server.v1_8_R2.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftEntity;
import org.bukkit.util.Vector;
import us.mcmagic.magicassistant.MagicAssistant;

import java.util.UUID;

/**
 * Created by Marc on 4/1/15
 */
public class Cart extends EntityMinecartRideable {
    private static final int[][][] matrix = new int[][][]{{{0, 0, -1}, {0, 0, 1}}, {{-1, 0, 0}, {1, 0, 0}},
            {{-1, -1, 0}, {1, 0, 0}}, {{-1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}},
            {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, {-1, 0, 0}}, {{0, 0, -1}, {-1, 0, 0}}, {{0, 0, -1}, {1, 0, 0}}};
    private Train train;
    private UUID passenger;
    private boolean atStation = false;
    private Station station;
    private boolean slowdown = false;
    private boolean playerEnter = true;
    private boolean playerExit;
    private double power = 0.2;

    public Cart(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
        CraftEntity e;
    }

    public void move(double dx, double dy, double dz) {
        Location from = new Location(this.getWorld().getWorld(), locX, locY, locZ);
        double x = motX;
        double y = motY;
        double z = motZ;
        Vector v = getFlyingVelocityMod();
        x *= v.getX();
        y *= v.getY();
        z *= v.getZ();
        //Bukkit.broadcastMessage(v.getX() + ", " + v.getY() + "," + v.getZ());
        Location to = from.clone().add(motX, motY, motZ);
        CartMoveEvent e = new CartMoveEvent(this, from, to);
        Bukkit.getPluginManager().callEvent(e);
        if (!e.isCancelled()) {
            Block b = from.getWorld().getBlockAt(RideManager.locInt(locX), RideManager.locInt(locY), RideManager.locInt(locZ));
            if (MagicAssistant.rideManager.isRail(b.getLocation())) {
                BlockMinecartTrackAbstract.EnumTrackPosition pos = MagicAssistant.rideManager.getTrackPosition(b);
                int[][] aint = matrix[pos.a()];
                double d1 = (double) (aint[1][0] - aint[0][0]);
                double d2 = (double) (aint[1][2] - aint[0][2]);
                double d3 = Math.sqrt(d1 * d1 + d2 * d2);
                double d5 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
                this.motX = d5 * d1 / d3;
                this.motZ = d5 * d2 / d3;
                super.velocityChanged = true;
                return;
            }
        }
        super.motX = 0.0;
        super.motY = 0.0;
        super.motZ = 0.0;
        super.positionChanged = false;
    }

    /*
    public void move(double x, double y, double z) {
        Location from = new Location(this.getWorld().getWorld(), locX, locY, locZ);
        Location to = from.clone().add(x, y, z);
        CartMoveEvent e = new CartMoveEvent(this, from, to);
        Bukkit.getPluginManager().callEvent(e);
        if (!e.isCancelled()) {
            if (!slowdown) {
                updateSpeed();
                setSpeed(getSpeed());
            }
            super.move(x, y, z);
            return;
        }
        super.motX = 0.0;
        super.motY = 0.0;
        super.motZ = 0.0;
        super.positionChanged = false;
    }
    */

    @Override
    public void die() {
        if (atStation) {
            return;
        }
        CartDestroyEvent e = new CartDestroyEvent(this);
        if (!e.isCancelled()) {
            getBukkitEntity().getWorld().playSound(getBukkitEntity().getLocation(), Sound.FIZZ, 10, 2);
            super.die();
        }
    }

    public boolean hasPassenger() {
        return passenger != null;
    }

    public void setPassenger(UUID passenger) {
        this.passenger = passenger;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getPower() {
        return power;
    }

    public Station getStation() {
        return station;
    }

    public void removeStation() {
        station = null;
        atStation = false;
    }

    public boolean isAtStation() {
        return atStation;
    }

    public void setStation(Station s) {
        atStation = s != null;
        station = s;
        Bukkit.getScheduler().runTaskLater(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                setPower(station.getLaunchPower());
                removeStation();
            }
        }, (long) (s.getLength() * 20));
    }

    public UUID getPassenger() {
        return passenger;
    }

    private void checkTrainNotNull() {
        if (train == null) {
            train = new Train(this);
        }
    }

    public void addCartToTrain(Cart cart) {
        checkTrainNotNull();
        train.addCart(cart);
    }

    public void removeCartFromTrain(Cart cart) {
        checkTrainNotNull();
        train.removeCart(cart);
    }

    public Train getTrain() {
        checkTrainNotNull();
        return train;
    }

    @Override
    public EnumMinecartType s() {
        return EnumMinecartType.RIDEABLE;
    }

    public void setPlayerEnter(boolean playerEnter) {
        this.playerEnter = playerEnter;
    }

    public boolean playerCanEnter() {
        return playerEnter;
    }

    public void setPlayerExit(boolean playerExit) {
        this.playerExit = playerExit;
    }

    public boolean playerCanExit() {
        return playerExit;
    }

    public boolean canSlowdown() {
        return slowdown;
    }

    public void setSlowdown(boolean slowdown) {
        this.slowdown = slowdown;
    }
}