package dev.defiled.map.frame;

import dev.defiled.map.Data;
import dev.defiled.map.coordination.Tile;
import dev.defiled.map.test.ClientAStar;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Scanner;

public class MapFrame extends JPanel {

    private BufferedImage image;

    private static JFrame frame;

    private static MapFrame panel;

    private boolean grid = false;

    private Markings markings;

    public double zoom = 1.0;
    public static final double SCALE_STEP = 0.1d;
    private Dimension initialSize;
    public double previousZoom = zoom;
    private double scrollX = 0d;
    private double scrollY = 0d;

    private double previousZoomPrev = zoom;

    private boolean displayFlags = false;

    public static void main(String[] args) {
        panel = new MapFrame();

        frame = new JFrame("Map");
        frame.setSize(1800, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new JScrollPane(panel));
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public MapFrame() {
        loadImage();
        addMouseMotionListener(ma);
        addMouseListener(ma);
        addMouseWheelListener(ma);
        setFocusable(true);
        addKeyListener(ka);
        setVisible(true);
        setAutoscrolls(true);
        setPreferredSize(new Dimension((int) (image.getWidth()), (int) (image.getHeight())));
        markings = new Markings(this);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //Obtain a copy of graphics object without any transforms
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.clearRect(0, 0, getWidth(), getHeight());

        //Zoom graphics
        g2d.scale(zoom, zoom);

        //translate graphics to be always in center of the canvas
        Rectangle size = getBounds();
        double tx = ((size.getWidth() - image.getWidth() * zoom) / 2) / zoom;
        double ty = ((size.getHeight() - image.getHeight() * zoom) / 2) / zoom;
        g2d.translate(tx, ty);


        g2d.drawImage(image, 0, 0, this);

        grid(g2d, image.getWidth(), image.getHeight());

        //grid(g, image.getWidth(),image.getHeight());

        g2d.dispose();
    }

    private void grid(Graphics2D g, int w, int h) {
        g.setColor(new Color(0, 255, 255, 180));

        int PIXEL_HEIGHT_DIFFERENCE = h / Data.GRID_HEIGHT_BOXES_COUNT;
        int PIXEL_WIDTH_DIFFERENCE = w / Data.GRID_WIDTH_BOXES_COUNT;

        if (grid) {
            int start = 0;

            for (int i = 0; i < Data.GRID_HEIGHT_BOXES_COUNT; i++)
                g.drawLine(0, start = start + PIXEL_HEIGHT_DIFFERENCE, w, start);

            start = 0;

            for (int i = 0; i < Data.GRID_WIDTH_BOXES_COUNT; i++)
                g.drawLine(start = start + PIXEL_WIDTH_DIFFERENCE, 0, start, h);
        }

        //Test 2142 3903 works  //3592,3837 fossil island isle

        g.setColor(new Color(255, 0, 0, 90));

        for (Tile t : Markings.savedTiles) {
            Point p = t.getPositionOnMap();
            g.fillRect(p.x, p.y, Data.TILE_SIZE, Data.TILE_SIZE);
        }

        g.setColor(new Color(0, 255, 255, 180));

        for (Tile t : Markings.currentlyMarked) {
            Point p = t.getPositionOnMap();
            g.fillRect(p.x, p.y, Data.TILE_SIZE, Data.TILE_SIZE);
        }


        markings.highlightHovered(g);
    }

    MouseAdapter ma = new MouseAdapter() {

        private Point startPoint;

        int count = 0;

        Tile start;

        @Override
        public void mousePressed(MouseEvent e) {
            startPoint = e.getPoint();
            if (e.getButton() == 3) {
                if (count == 0) {
                    Tile t = Markings.tileFromPoint(getZoomedPoint(startPoint));
                    start = new Tile(t.getX(), t.getY());
                    count++;
                } else if (count == 1) {
                    long time = System.currentTimeMillis();
                    Tile t = Markings.tileFromPoint(getZoomedPoint(startPoint));
                    ClientAStar cas = new ClientAStar(new Tile(start.getX(), start.getY()), new Tile(t.getX(), t.getY()));
                    dev.defiled.map.test.AStarNode pathNode = cas.findPath();
                    if (pathNode != null) {
                        dev.defiled.map.test.AStarNode curr = pathNode;
                        Markings.currentlyMarked.add(new Tile(curr.getX(), curr.getY()));
                        while (curr.getPrev() != null) {
                            Markings.currentlyMarked.add(new Tile(curr.getPrev().getX(), curr.getPrev().getY()));
                            curr = curr.getPrev();
                        }
                        repaint();
                    }
                    count++;
                    System.out.println(System.currentTimeMillis() - time);
                } else {
                    Markings.savedTiles.clear();
                    Markings.currentlyMarked.clear();
                    repaint();
                    count = 0;
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (e.getButton() == 2 && startPoint != null) {
                int deltaX = startPoint.x - e.getX();
                int deltaY = startPoint.y - e.getY();
                Rectangle view = getVisibleRect();
                view.x += deltaX;
                view.y += deltaY;
                scrollRectToVisible(view);
            }
        }

        public void mouseWheelMoved(MouseWheelEvent e) {
            double zoomFactor = -SCALE_STEP * e.getPreciseWheelRotation() * zoom;
            zoom = Math.abs(zoom + zoomFactor);
            //Here we calculate new size of canvas relative to zoom.
            Dimension d = new Dimension(
                    (int) (initialSize.width * zoom),
                    (int) (initialSize.height * zoom));
            setPreferredSize(d);
            setSize(d);
            validate();
            followMouseOrCenter(e.getPoint());
            previousZoomPrev = previousZoom;
            previousZoom = zoom;
        }

        public void followMouseOrCenter(Point2D point) {
            Rectangle size = getBounds();
            Rectangle visibleRect = getVisibleRect();
            scrollX = size.getCenterX();
            scrollY = size.getCenterY();
            if (point != null) {
                scrollX = point.getX() / previousZoom * zoom - (point.getX() - visibleRect.getX());
                scrollY = point.getY() / previousZoom * zoom - (point.getY() - visibleRect.getY());
            }

            visibleRect.setRect(scrollX, scrollY, visibleRect.getWidth(), visibleRect.getHeight());
            scrollRectToVisible(visibleRect);
        }

        public Point getZoomedPoint(Point2D point) {
            scrollX = point.getX() / zoom;
            scrollY = point.getY() / zoom;
            return new Point((int) scrollX, (int) scrollY);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            Markings.current = getZoomedPoint(e.getPoint());
            repaint();
        }

    };

    @Override
    public void setSize(Dimension size) {
        super.setSize(size);
        if (initialSize == null) {
            this.initialSize = size;
        }
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        if (initialSize == null) {
            this.initialSize = preferredSize;
        }
    }

    KeyAdapter ka = new KeyAdapter() {

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyChar()) {
                case 'G':
                case 'g':
                    grid = !grid;
                    repaint();
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }

    };

    private void loadImage() {
        String fileName = "/Users/defiled/Desktop/Resources/osrs_world_map_january22_2021.png";
        try {
            image = ImageIO.read(new File(fileName));
        } catch (MalformedURLException mue) {
            System.out.println("URL trouble: " + mue.getMessage());
        } catch (IOException ioe) {
            System.out.println("read trouble: " + ioe.getMessage());
        }
    }

    public static MapFrame getPanel() {
        return panel;
    }
}



