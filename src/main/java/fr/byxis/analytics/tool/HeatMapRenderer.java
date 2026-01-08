package fr.byxis.analytics.tool;

import fr.byxis.analytics.LogEntry;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class HeatMapRenderer
{
    private Color getBlockColor(Material mat)
    {
        if (mat == Material.WATER) return new Color(64, 64, 255);
        if (mat == Material.LAVA) return new Color(255, 100, 0);
        if (mat.name().contains("GRASS")) return new Color(100, 160, 60);
        if (mat.name().contains("SAND")) return new Color(240, 240, 180);
        if (mat.name().contains("STONE") || mat.name().contains("DEEPSLATE")) return new Color(120, 120, 120);
        if (mat.name().contains("LEAVES")) return new Color(40, 100, 20);
        if (mat == Material.SNOW || mat == Material.SNOW_BLOCK) return Color.WHITE;
        return new Color(50, 50, 50); // Couleur par défaut (Gris foncé)
    }

    // Cette méthode doit être appelée bout par bout (voir section suivante)
    public void drawBlockPixel(BufferedImage img, int x, int z, World world, int minX, int minZ, int yMax, int yMin)
    {
        // Conversion coordonnées monde -> pixel image
        int px = x - minX;
        int py = z - minZ;

        if (px < 0 || py < 0 || px >= img.getWidth() || py >= img.getHeight()) return;

        // Trouver le bloc le plus haut visible à cette coordonnée
        // On part du haut et on descend
        for (int y = yMax; y >= yMin; y--)
        {
            Block b = world.getBlockAt(x, y, z);
            if (b.getType() != Material.AIR && b.getType() != Material.CAVE_AIR)
            {
                img.setRGB(px, py, getBlockColor(b.getType()).getRGB());
                return; // On a trouvé le sol, on arrête
            }
        }
        // Si vide (trou jusqu'au fond)
        img.setRGB(px, py, Color.BLACK.getRGB());
    }

    // --- PARTIE 2 : Génération de la Heatmap ---

    public void generateHeatmap(File baseImageFile, List<LogEntry> data, File outputFile) throws IOException
    {
        // 1. Charger l'image de fond
        BufferedImage base = ImageIO.read(baseImageFile);
        int width = base.getWidth();
        int height = base.getHeight();

        // 2. Créer un calque transparent pour la chaleur
        BufferedImage heatLayer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = heatLayer.createGraphics();

        // Configurer le mélangeur pour que les couleurs s'additionnent
        // "Composite" normal suffit si on joue sur l'alpha (transparence)

        // 3. Dessiner les points "chauds"
        // Astuce : On dessine des cercles flous très transparents
        float radius = 15.0f; // Rayon du point de chaleur
        float intensity = 0.1f; // Opacité par joueur (10% par passage)

        // Couleur du "point" (Blanc avec transparence)
        Color pointColor = new Color(1.0f, 1.0f, 1.0f, intensity);

        for (LogEntry entry : data)
        {
            /*
            // Conversion Coord MC -> Pixel (A ajuster selon ton mapping)
            // Supposons ici que l'image fait exactement la taille de la zone minX->maxX
            // Tu devras passer minX/minZ en paramètre
            int px = entry.getDate().g - (-2000); // Exemple hardcodé
            int py = entry.z - (-2000);

            if (px >= 0 && px < width && py >= 0 && py < height)
            {
                // On utilise un dégradé radial pour simuler le "Flou"
                RadialGradientPaint p = new RadialGradientPaint(
                        new Point(px, py),
                        radius,
                        new float[]{0.0f, 1.0f},
                        new Color[]{pointColor, new Color(1f, 1f, 1f, 0f)} // Blanc -> Transparent
                );
                g2d.setPaint(p);
                g2d.fillOval((int) (px - radius), (int) (py - radius), (int) (radius * 2), (int) (radius * 2));
            }
             */
        }
        g2d.dispose();

        // 4. Colorisation (Fausse couleur)
        // Pour l'instant "heatLayer" est en niveaux de blanc transparent.
        // On va convertir l'intensité (Alpha) en couleur (Bleu -> Rouge)
        BufferedImage finalOverlay = colorizeHeatmap(heatLayer);

        // 5. Fusionner avec le fond
        Graphics2D gFinal = base.createGraphics();
        gFinal.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f)); // Transparence globale
        gFinal.drawImage(finalOverlay, 0, 0, null);
        gFinal.dispose();

        // 6. Sauvegarder
        ImageIO.write(base, "png", outputFile);
    }

    // Convertit le calque Blanc/Transparent en Bleu/Rouge
    private BufferedImage colorizeHeatmap(BufferedImage grayscale)
    {
        int w = grayscale.getWidth();
        int h = grayscale.getHeight();
        BufferedImage colorized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < w; x++)
        {
            for (int y = 0; y < h; y++)
            {
                int argb = grayscale.getRGB(x, y);
                int alpha = (argb >> 24) & 0xFF; // On récupère l'opacité accumulée

                if (alpha == 0) continue; // Pas de chaleur ici

                // Créer la couleur selon l'intensité (Heatmap Gradient)
                // Alpha 0 -> Bleu, Alpha 128 -> Vert, Alpha 255 -> Rouge
                Color c = getHeatColor(alpha);

                // On remet une opacité fixe pour l'affichage (ex: 80%)
                // ou on garde l'alpha calculé pour que les zones froides soient transparentes
                colorized.setRGB(x, y, new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.min(200, alpha * 2)).getRGB());
            }
        }
        return colorized;
    }

    private Color getHeatColor(int intensity)
    {
        // Simple dégradé Bleu -> Vert -> Rouge -> Blanc
        float val = intensity / 255.0f;
        return Color.getHSBColor((1.0f - val) * 0.7f, 1.0f, 1.0f); // HSB trick
    }
}
