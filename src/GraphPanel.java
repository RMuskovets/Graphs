import org.matheclipse.parser.client.eval.DoubleEvaluator;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphPanel extends JPanel {

    private Color pointColor = new Color(100, 100, 100, 180);
    private List<Point> scores;

    static Map<String, Double> VARIABLES = new HashMap<>();

    private GraphPanel(List<Point> scores) {
        this.scores = scores;
    }

    //@SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int labelPadding = 0;
        int padding = 0;

        List<Point> graphPoints = scores;
        // draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding, getHeight() - 2 * padding - labelPadding);
        g2.setColor(Color.BLACK);
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding, getHeight() - padding - labelPadding);

        Stroke oldStroke = g2.getStroke();

        g2.setStroke(oldStroke);
        g2.setColor(pointColor);
        loop:
        for (Point graphPoint : graphPoints) {
            int x = graphPoint.x - 4 / 2;
            int y = graphPoint.y - 4 / 2;
            g2.fillOval(x, y, 4, 4);
            int index = graphPoints.indexOf(graphPoint);
            try {
                int x2 = graphPoints.get(index + 1).x - 4 / 2;
                int y2 = graphPoints.get(index + 1).y - 4 / 2;
                g2.drawLine(x, y, x2, y2);
            } catch (Exception e) {}
        }
    }

    private static int floor(double d) {
        return (int) (d * 10 % 10 < 5 ? d : d + 1);
    }

    private static void createAndShowGui() {
        List<Point> scores = new ArrayList<>();
        JTabbedPane tabs = new JTabbedPane();
        GraphPanel mainPanel = new GraphPanel(scores);
        mainPanel.setPreferredSize(new Dimension(800, 600));
        JPanel frame = new JPanel();
        frame.setLayout(new BorderLayout());
        JTextField equality = new JTextField("Cos[x] / Sin[x]");
        equality.addActionListener(e -> {
            String equal = equality.getText();
            DoubleEvaluator eval = new DoubleEvaluator();
            mainPanel.scores.clear();
            for (int x = 0; x < mainPanel.getWidth(); x++) {
                int cx = x - (mainPanel.getWidth() / 2);
                VARIABLES.put("x", (double) cx);
                putVariables(eval);
                int cy = floor(eval.evaluate(equal));
                mainPanel.scores.add(new Point(x, cy + (mainPanel.getHeight() / 2)));
                System.out.println(eval.evaluate(equal));
                mainPanel.repaint();
            }
        });
        frame.add(equality, "North");
        frame.add(mainPanel, "Center");

        tabs.addTab("Graph solver", frame);
        tabs.addTab("Program variables", new VariablePanel());

        JFrame jfr = new JFrame("Graphs 2.0-ALPHA");
        jfr.setContentPane(tabs);
        jfr.setLocationRelativeTo(null);
        jfr.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jfr.setVisible(true);
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new NimbusLookAndFeel());
        System.setSecurityManager(new Security());
        SwingUtilities.invokeLater(GraphPanel::createAndShowGui);
    }

    private static void putVariables(DoubleEvaluator eval) {
        for (Map.Entry<String, Double> x : VARIABLES.entrySet()) {
            eval.defineVariable(x.getKey(), x.getValue());
        }
    }
}