package fr.byxis.analytics.tool;

import fr.byxis.fireland.Fireland;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Logger;

public class AsyncMapScanner
{

    private final Fireland m_fireland;
    private final HeatMapRenderer renderer = new HeatMapRenderer();

    // Config
    private final int m_minX = -5000, m_maxX = 5000;
    private final int m_minZ = -5000, m_maxZ = 5000;

    public AsyncMapScanner(Fireland _fireland)
    {
        this.m_fireland = _fireland;
    }

    public void startScan(String _layerName, int _yMin, int _yMax)
    {
        int width = m_maxX - m_minX;
        int height = m_maxZ - m_minZ;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        World world = Bukkit.getWorld("world");

        Logger.getLogger("Fireland").info("Starting async map scan for layer: " + _layerName);

        new BukkitRunnable()
        {
            private int m_currentX = m_minX;
            private int m_speed = 500;

            @Override
            public void run()
            {
                long start = System.currentTimeMillis();

                for (int i = 0; i < 20; i++)
                {
                    if (m_currentX >= m_maxX)
                    {
                        finish();
                        return;
                    }

                    for (int z = m_minZ; z < m_maxZ; z++)
                    {
                        renderer.drawBlockPixel(image, m_currentX, z, world, m_minX, m_minZ, _yMax, _yMin);
                    }
                    m_currentX++;
                }

                if (System.currentTimeMillis() - start > 40)
                {
                    m_speed = Math.max(50, m_speed - 50);
                }
                else if (System.currentTimeMillis() - start < 10)
                {
                    m_speed = Math.min(1000, m_speed + 50);
                }
            }

            private void finish()
            {
                this.cancel();
                new Thread(() ->
                {
                    try
                    {
                        File output = new File(m_fireland.getDataFolder(), "map_" + _layerName + ".png");
                        ImageIO.write(image, "png", output);
                        Logger.getLogger("Fireland").info("Map scan for layer " + _layerName + " completed. Output: " + output.getAbsolutePath());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }).start();
            }
        }.runTaskTimer(m_fireland, 0L, 1L);
    }
}