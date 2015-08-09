package us.mcmagic.magicassistant.show;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import us.mcmagic.magicassistant.show.actions.*;
import us.mcmagic.magicassistant.utils.MathUtil;
import us.mcmagic.magicassistant.utils.WorldUtil;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.title.TitleObject;

import java.io.*;
import java.util.*;

public class Show {
    private World world;
    private Location loc;
    private int radius = 75;
    private long startTime;
    public HashSet<ShowAction> actions;
    private HashMap<String, FireworkEffect> effectMap;
    private HashMap<String, String> invalidLines;
    private HashMap<String, ShowNPC> npcMap;
    private int npcTick = 0;
    private long lastPlayerListUpdate = System.currentTimeMillis();
    private List<Player> nearbyPlayers = new ArrayList<>();

    public Show(JavaPlugin plugin, File file) {
        world = Bukkit.getWorlds().get(0);
        effectMap = new HashMap<>();
        invalidLines = new HashMap<>();
        npcMap = new HashMap<>();
        loadActions(file);
        startTime = System.currentTimeMillis();
        for (Player tp : Bukkit.getOnlinePlayers()) {
            if (tp.getLocation().distance(loc) <= radius) {
                nearbyPlayers.add(tp);
            }
        }
    }

    private void loadActions(File file) {
        actions = new HashSet<>();
        String strLine = "";
        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            // Parse Lines
            while ((strLine = br.readLine()) != null) {
                if (strLine.length() == 0 || strLine.startsWith("#"))
                    continue;
                String[] tokens = strLine.split("\\s+");
                if (tokens.length < 3) {
                    System.out.println("Invalid Show Line [" + strLine + "]");
                }
                // Set Show Location
                if (tokens[1].equals("Location")) {
                    Location loc = WorldUtil.strToLoc(world.getName() + "," + tokens[2]);
                    if (loc == null) {
                        invalidLines.put(strLine, "Invalid Location Line");
                        continue;
                    }
                    this.loc = loc;
                    continue;
                }
                //Load other show
                if (tokens[1].equals("LoadShow")) {
                    String showName = tokens[2];
                    File f = new File("plugins/MagicAssistant/shows/" + showName);
                    if (!f.exists()) {
                        invalidLines.put(strLine, "Show does not exist!");
                        continue;
                    }
                    if (f.equals(file)) {
                        invalidLines.put(strLine, "You cannot load a file that's already being loaded");
                        continue;
                    }
                    loadActions(f);
                    continue;
                }
                // Set Text Radius
                if (tokens[1].equals("TextRadius")) {
                    try {
                        radius = Integer.parseInt(tokens[2]);
                    } catch (Exception e) {
                        invalidLines.put(strLine, "Invalid Text Radius");
                    }
                    continue;
                }
                // Load Firework effects
                if (tokens[0].equals("Effect")) {
                    FireworkEffect effect = parseEffect(tokens[2]);
                    if (effect == null) {
                        invalidLines.put(strLine, "Invalid Effect Line");
                        continue;
                    }
                    effectMap.put(tokens[1], effect);
                    continue;
                }
                // Get time
                String[] timeToks = tokens[0].split("_");
                long time = 0;
                for (String timeStr : timeToks) {
                    time += (long) (Double.parseDouble(timeStr) * 1000);
                }
                // Text
                if (tokens[1].contains("Text")) {
                    String text = "";
                    for (int i = 2; i < tokens.length; i++)
                        text += tokens[i] + " ";
                    if (text.length() > 1)
                        text = text.substring(0, text.length() - 1);
                    actions.add(new TextAction(this, time, text));
                }
                // Music
                if (tokens[1].contains("Music")) {
                    try {
                        int id = Integer.parseInt(tokens[2]);
                        actions.add(new MusicAction(this, time, id));
                    } catch (Exception e) {
                        invalidLines.put(strLine, "Invalid Material");
                    }
                }
                // Pulse
                if (tokens[1].contains("Pulse")) {
                    Location loc = WorldUtil.strToLoc(world.getName() + "," + tokens[2]);
                    if (loc == null) {
                        invalidLines.put(strLine, "Invalid Location");
                        continue;
                    }
                    actions.add(new PulseAction(this, time, loc));
                }
                // Lightning
                if (tokens[1].contains("Lightning")) {
                    Location loc = WorldUtil.strToLoc(world.getName() + "," + tokens[2]);
                    if (loc == null) {
                        invalidLines.put(strLine, "Invalid Location");
                        continue;
                    }
                    actions.add(new LightningAction(this, time, loc));
                }
                // NPC Spawn
                if (tokens[1].contains("NPC")) {
                    // 0 NPC Spawn Name x,y,z Type MaterialInHand
                    if (tokens.length < 4) {
                        invalidLines.put(strLine, "Invalid NPC Line");
                        continue;
                    }
                    String name = tokens[3];
                    // Spawn
                    if (tokens[2].contains("Spawn")) {
                        if (tokens.length < 5) {
                            invalidLines.put(strLine, "Invalid NPC Spawn Line");
                            continue;
                        }
                        // type
                        EntityType type;
                        if (tokens.length >= 6) {
                            try {
                                type = EntityType.valueOf(tokens[5]);
                            } catch (Exception e) {
                                invalidLines.put(strLine, "Invalid NPC Spawn Line: Entity Type");
                                continue;
                            }
                        } else {
                            type = EntityType.SKELETON;
                        }
                        Material holding = null;
                        if (tokens.length >= 7) {
                            try {
                                holding = Material.valueOf(tokens[6]);
                            } catch (Exception e) {
                                invalidLines.put(strLine,
                                        "Invalid NPC Spawn Line: Item In Hand");
                                continue;
                            }
                        }
                        // Loc
                        Location loc = WorldUtil.strToLoc(world.getName() + "," + tokens[4]);
                        if (loc == null) {
                            invalidLines.put(strLine, "Invalid Location");
                            continue;
                        }
                        // Add
                        actions.add(new NPCSpawnAction(this, time, name, loc, type, holding));
                    }
                    // Remove
                    if (tokens[2].contains("Remove")) {
                        actions.add(new NPCRemoveAction(this, time, name));
                    }
                    // move
                    if (tokens[2].contains("Move")) {
                        if (tokens.length < 5) {
                            invalidLines.put(strLine, "Invalid NPC Line");
                            continue;
                        }
                        // Speed
                        float speed = 1f;
                        if (tokens.length >= 6) {
                            try {
                                speed = Float.valueOf(tokens[5]);
                            } catch (Exception e) {
                                invalidLines.put(strLine, "Invalid NPC Spawn Line");
                                continue;
                            }
                        }
                        // Loc
                        Location loc = WorldUtil.strToLoc(world.getName() + "," + tokens[4]);
                        if (loc == null) {
                            invalidLines.put(strLine, "Invalid Location");
                            continue;
                        }
                        actions.add(new NPCMoveAction(this, time, name, loc, speed));
                    }
                }
                // Fake Block
                if (tokens[1].contains("FakeBlock")) {
                    Location loc = WorldUtil.strToLoc(world.getName() + "," + tokens[3]);
                    if (loc == null) {
                        invalidLines.put(strLine, "Invalid Location");
                        continue;
                    }
                    String[] list;
                    if (tokens[2].contains(":")) {
                        list = tokens[2].split(":");
                    } else {
                        list = null;
                    }
                    try {
                        int id;
                        byte data;
                        if (list != null) {
                            id = Integer.parseInt(list[0]);
                            data = Byte.parseByte(list[1]);
                        } else {
                            id = Integer.parseInt(tokens[2]);
                            data = (byte) 0;
                        }
                        actions.add(new FakeBlockAction(this, time, loc, id, data));
                    } catch (Exception e) {
                        e.printStackTrace();
                        invalidLines.put(strLine,
                                "Invalid Block ID or Block data");
                    }
                }
                // Block
                if (tokens[1].contains("Block")) {
                    Location loc = WorldUtil.strToLoc(world.getName() + "," + tokens[3]);
                    if (loc == null) {
                        invalidLines.put(strLine, "Invalid Location");
                        continue;
                    }
                    String[] list;
                    if (tokens[2].contains(":")) {
                        list = tokens[2].split(":");
                    } else {
                        list = null;
                    }
                    try {
                        int id;
                        byte data;
                        if (list != null) {
                            id = Integer.parseInt(list[0]);
                            data = Byte.parseByte(list[1]);
                        } else {
                            id = Integer.parseInt(tokens[2]);
                            data = (byte) 0;
                        }
                        actions.add(new BlockAction(this, time, loc, id, data));
                    } catch (Exception e) {
                        invalidLines.put(strLine, "Invalid Block ID or Block data");
                    }
                }
                // Firework
                if (tokens[1].contains("PowerFirework")) {
                    if (tokens.length != 5) {
                        invalidLines.put(strLine, "Invalid PowerFirework Line Length");
                    }
                    Location loc = WorldUtil.strToLoc(Bukkit.getWorlds().get(0).getName() + "," + tokens[2]);
                    String[] l = tokens[4].split(",");
                    Vector motion = new Vector(Double.parseDouble(l[0]), Double.parseDouble(l[1]),
                            Double.parseDouble(l[2]));
                    ArrayList<FireworkEffect> effectList = new ArrayList<>();
                    String[] effects = tokens[3].split(",");
                    for (String effect : effects) {
                        if (effectMap.containsKey(effect)) {
                            effectList.add(effectMap.get(effect));
                        }
                    }
                    if (effectList.isEmpty()) {
                        invalidLines.put(strLine, "Invalid effects");
                        continue;
                    }
                    actions.add(new PowerFireworkAction(this, time, loc, motion, effectList));
                }
                if (tokens[1].startsWith("Firework")) {
                    if (tokens.length != 7) {
                        invalidLines.put(strLine, "Invalid Firework Line Length");
                        continue;
                    }
                    // loc
                    Location loc = WorldUtil.strToLoc(world.getName() + "," + tokens[2]);
                    if (loc == null) {
                        invalidLines.put(strLine, "Invalid Location");
                        continue;
                    }
                    // Effect List
                    ArrayList<FireworkEffect> effectList = new ArrayList<>();
                    String[] effects = tokens[3].split(",");
                    for (String effect : effects) {
                        if (effectMap.containsKey(effect)) {
                            effectList.add(effectMap.get(effect));
                        }
                    }
                    if (effectList.isEmpty()) {
                        invalidLines.put(strLine, "Invalid effects");
                        continue;
                    }
                    // power
                    int power;
                    try {
                        power = Integer.parseInt(tokens[4]);
                        if (power < 0 || power > 5) {
                            invalidLines.put(strLine, "Power too High/Low");
                            continue;
                        }
                    } catch (Exception e) {
                        invalidLines.put(strLine, "Invalid Power");
                        continue;
                    }
                    // direction
                    Vector dir;
                    try {
                        String[] coords = tokens[5].split(",");
                        dir = new Vector(Double.parseDouble(coords[0]),
                                Double.parseDouble(coords[1]),
                                Double.parseDouble(coords[2]));
                    } catch (Exception e) {
                        invalidLines.put(strLine, "Invalid Direction");
                        continue;
                    }
                    // Dir power
                    double dirPower;
                    try {
                        dirPower = Double.parseDouble(tokens[6]);
                        if (dirPower < 0 || dirPower > 10) {
                            invalidLines.put(strLine, "Direction Power too High/Low");
                            continue;
                        }
                    } catch (Exception e) {
                        invalidLines.put(strLine, "Invalid Direction Power");
                        continue;
                    }
                    actions.add(new FireworkAction(this, time, loc, effectList, power, dir, dirPower));
                }
                // Schematic
                if (tokens[1].contains("Schematic")) {
                    if (isInt(tokens[3]) && isInt(tokens[4]) && isInt(tokens[5])) {
                        int x = Integer.parseInt(tokens[3]);
                        int y = Integer.parseInt(tokens[4]);
                        int z = Integer.parseInt(tokens[5]);
                        Location pasteloc = new Location(
                                Bukkit.getWorld(tokens[6]), x, y, z);
                        WorldEditPlugin wep = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
                        File schemfile = new File(wep.getDataFolder().getPath() + "/schematics/" + tokens[2] + ".schematic");
                        boolean noAir;
                        noAir = !tokens[7].toLowerCase().contains("false");
                        actions.add(new SchematicAction(this, time, pasteloc, schemfile, noAir));
                    }
                }
                if (tokens[1].contains("Fountain")) {
                    Location loc = WorldUtil.strToLoc(world.getName() + "," + tokens[4]);
                    Double[] values = WorldUtil.strToDoubleList(world.getName() + "," + tokens[5]);
                    double duration = Double.parseDouble(tokens[3]);
                    String[] list;
                    if (tokens[2].contains(":")) {
                        list = tokens[2].split(":");
                    } else {
                        list = null;
                    }
                    try {
                        int type;
                        byte data;
                        if (list != null) {
                            type = Integer.parseInt(list[0]);
                            data = Byte.parseByte(list[1]);
                        } else {
                            type = Integer.parseInt(tokens[2]);
                            data = (byte) 0;
                        }
                        Vector force = new Vector(values[0], values[1], values[2]);
                        actions.add(new FountainAction(this, time, loc, duration, type, data, force));
                    } catch (NumberFormatException e) {
                        invalidLines.put(strLine, "Invalid Fountain Type");
                        e.printStackTrace();
                    }
                }
                if (tokens[1].contains("Title")) {
                    // 0 Title title fadeIn fadeOut stay title...
                    TitleObject.TitleType type = TitleObject.TitleType.valueOf(tokens[2].toUpperCase());
                    int fadeIn = Integer.parseInt(tokens[3]);
                    int fadeOut = Integer.parseInt(tokens[4]);
                    int stay = Integer.parseInt(tokens[5]);
                    String text = "";
                    for (int i = 6; i < tokens.length; i++)
                        text += tokens[i] + " ";
                    if (text.length() > 1)
                        text = text.substring(0, text.length() - 1);
                    actions.add(new TitleAction(this, time, type, text, fadeIn, fadeOut, stay));
                }
                if (tokens[1].contains("Particle")) {
                    // 0 Particle type x,y,z oX oY oZ speed amount
                    ParticleEffect effect = ParticleEffect.fromString(tokens[2]);
                    Location location = WorldUtil.strToLoc(world.getName() + "," + tokens[3]);
                    double offsetX = Float.parseFloat(tokens[4]);
                    double offsetY = Float.parseFloat(tokens[5]);
                    double offsetZ = Float.parseFloat(tokens[6]);
                    float speed = Float.parseFloat(tokens[7]);
                    int amount = Integer.parseInt(tokens[8]);
                    actions.add(new ParticleAction(this, time, effect, location, offsetX, offsetY, offsetZ, speed, amount));
                }
            }
            in.close();
        } catch (Exception e) {
            System.out.println("Error on Line [" + strLine + "]");
            Bukkit.broadcast("Error on Line [" + strLine + "]", "arcade.bypass");
            e.printStackTrace();
        }

        if (loc == null) {
            invalidLines.put("Missing Line", "Show loc x,y,z");
        }

        for (String cur : invalidLines.keySet()) {
            System.out.print(ChatColor.GOLD + invalidLines.get(cur) + " @ " + ChatColor.WHITE + cur.replaceAll("\t", " "));
            Bukkit.broadcast(ChatColor.GOLD + invalidLines.get(cur) + " @ " + ChatColor.WHITE + cur.replaceAll("\t", " "),
                    "arcade.bypass");
        }
    }

    public List<Player> getNearPlayers() {
        if (System.currentTimeMillis() - lastPlayerListUpdate < 10000) {
            return new ArrayList<>(nearbyPlayers);
        }
        List<Player> list = new ArrayList<>();
        for (Player tp : Bukkit.getOnlinePlayers()) {
            if (tp.getLocation().distance(loc) <= radius) {
                list.add(tp);
            }
        }
        lastPlayerListUpdate = System.currentTimeMillis();
        nearbyPlayers = list;
        return list;
    }

    public boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public boolean update() {
        if (!invalidLines.isEmpty()) {
            return true;
        }
        npcTick = (npcTick + 1) % 5;
        if (npcTick == 0) {
            for (ShowNPC npc : npcMap.values()) {
                npc.move();
            }
        }
        // Show Action
        HashSet<ShowAction> list = new HashSet<>(actions);
        for (ShowAction action : list) {
            if (System.currentTimeMillis() - startTime <= action.time) {
                continue;
            }
            action.play();
            actions.remove(action);
        }
        return actions.isEmpty();
    }

    public void displayText(String text) {
        for (Player player : getNearPlayers()) {
            if (MathUtil.offset(player.getLocation(), loc) < radius) {
                player.sendMessage(ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', text));
            }
        }
    }

    public void displayTitle(TitleObject title) {
        for (Player player : getNearPlayers()) {
            if (MathUtil.offset(player.getLocation(), loc) < radius) {
                title.send(player);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void playMusic(int record) {
        for (Player player : getNearPlayers()) {
            player.playEffect(loc, Effect.RECORD_PLAY, record);
        }
    }

    public FireworkEffect parseEffect(String effect) {
        String[] tokens = effect.split(",");

        // Shape
        Type shape;
        try {
            shape = Type.valueOf(tokens[0]);
        } catch (Exception e) {
            invalidLines.put(effect, "Invalid type [" + tokens[0] + "]");
            return null;
        }

        // Color
        ArrayList<Color> colors = new ArrayList<>();
        for (String color : tokens[1].split("&")) {
            if (color.equalsIgnoreCase("AQUA")) {
                colors.add(Color.AQUA);
            } else if (color.equalsIgnoreCase("BLACK")) {
                colors.add(Color.BLACK);
            } else if (color.equalsIgnoreCase("BLUE")) {
                colors.add(Color.BLUE);
            } else if (color.equalsIgnoreCase("FUCHSIA")) {
                colors.add(Color.FUCHSIA);
            } else if (color.equalsIgnoreCase("GRAY")) {
                colors.add(Color.GRAY);
            } else if (color.equalsIgnoreCase("GREEN")) {
                colors.add(Color.GREEN);
            } else if (color.equalsIgnoreCase("LIME")) {
                colors.add(Color.LIME);
            } else if (color.equalsIgnoreCase("MAROON")) {
                colors.add(Color.MAROON);
            } else if (color.equalsIgnoreCase("NAVY")) {
                colors.add(Color.NAVY);
            } else if (color.equalsIgnoreCase("OLIVE")) {
                colors.add(Color.OLIVE);
            } else if (color.equalsIgnoreCase("ORANGE")) {
                colors.add(Color.ORANGE);
            } else if (color.equalsIgnoreCase("PURPLE")) {
                colors.add(Color.PURPLE);
            } else if (color.equalsIgnoreCase("RED")) {
                colors.add(Color.RED);
            } else if (color.equalsIgnoreCase("SILVER")) {
                colors.add(Color.SILVER);
            } else if (color.equalsIgnoreCase("TEAL")) {
                colors.add(Color.TEAL);
            } else if (color.equalsIgnoreCase("WHITE")) {
                colors.add(Color.WHITE);
            } else if (color.equalsIgnoreCase("YELLOW")) {
                colors.add(Color.YELLOW);
            } else {
                invalidLines.put(effect, "Invalid Color [" + color + "]");
                return null;
            }
        }
        if (colors.isEmpty()) {
            invalidLines.put(effect, "No Valid Colors");
            return null;
        }
        // Fade
        ArrayList<Color> fades = new ArrayList<>();
        for (String color : tokens[1].split("&")) {
            if (color.equalsIgnoreCase("AQUA")) {
                fades.add(Color.AQUA);
            } else if (color.equalsIgnoreCase("BLACK")) {
                fades.add(Color.BLACK);
            } else if (color.equalsIgnoreCase("BLUE")) {
                fades.add(Color.BLUE);
            } else if (color.equalsIgnoreCase("FUCHSIA")) {
                fades.add(Color.FUCHSIA);
            } else if (color.equalsIgnoreCase("GRAY")) {
                fades.add(Color.GRAY);
            } else if (color.equalsIgnoreCase("GREEN")) {
                fades.add(Color.GREEN);
            } else if (color.equalsIgnoreCase("LIME")) {
                fades.add(Color.LIME);
            } else if (color.equalsIgnoreCase("MAROON")) {
                fades.add(Color.MAROON);
            } else if (color.equalsIgnoreCase("NAVY")) {
                fades.add(Color.NAVY);
            } else if (color.equalsIgnoreCase("OLIVE")) {
                fades.add(Color.OLIVE);
            } else if (color.equalsIgnoreCase("ORANGE")) {
                fades.add(Color.ORANGE);
            } else if (color.equalsIgnoreCase("PURPLE")) {
                fades.add(Color.PURPLE);
            } else if (color.equalsIgnoreCase("RED")) {
                fades.add(Color.RED);
            } else if (color.equalsIgnoreCase("SILVER")) {
                fades.add(Color.SILVER);
            } else if (color.equalsIgnoreCase("TEAL")) {
                fades.add(Color.TEAL);
            } else if (color.equalsIgnoreCase("WHITE")) {
                fades.add(Color.WHITE);
            } else if (color.equalsIgnoreCase("YELLOW")) {
                fades.add(Color.YELLOW);
            } else {
                invalidLines.put(effect, "Invalid Fade Color [" + color + "]");
                return null;
            }
        }
        if (fades.isEmpty()) {
            invalidLines.put(effect, "No Valid Fade Colors");
            return null;
        }
        boolean flicker = effect.toUpperCase().contains("FLICKER");
        boolean trail = effect.toUpperCase().contains("TRAIL");
        // Firework
        return FireworkEffect.builder().with(shape).withColor(colors).withFade(fades.get(0)).flicker(flicker).trail(trail).build();
    }

    public HashMap<String, ShowNPC> getNPCMap() {
        return npcMap;
    }
}
