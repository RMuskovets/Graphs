import com.google.common.graph.Graph;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VariablePanel extends JScrollPane {

    private List<Variable> vars = new ArrayList<>();

    private class Variable extends JPanel {
        String name;
        double value;

        public Variable(String name, double value) {
            super();
            this.name = name;
            this.value = value;
            setLayout(new HorizontalLayout());
            add(new JLabel("Name: " + name));
            JTextField val = new JTextField(String.valueOf(value));
            val.addActionListener(e -> {
                this.value = Double.valueOf(val.getText());
                GraphPanel.VARIABLES.put(name, value);
            });
            add(val);
            JButton del = new JButton("Delete");
            del.addActionListener(e -> {
                GraphPanel.VARIABLES.remove(name);
                vars.remove(this);
            });
            add(del);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Variable variable = (Variable) o;
            return Double.compare(variable.value, value) == 0 &&
                    Objects.equals(name, variable.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, value);
        }
    }

    public VariablePanel() {
        super();
        JPanel target = new JPanel();
        target.setLayout(new VerticalLayout());
        setViewportView(target);
        for (Map.Entry<String, Double> vr : GraphPanel.VARIABLES.entrySet()) {
            vars.add(new Variable(vr.getKey(), vr.getValue()));
        }

        for (Variable v : vars) {
            add(v);
        }

        final Map<String, Double>[] prev = new Map[]{GraphPanel.VARIABLES};
        new Timer(1000, e -> {
            if (!prev[0].equals(GraphPanel.VARIABLES)) {
                for (String key : GraphPanel.VARIABLES.keySet()) {
                    double value = GraphPanel.VARIABLES.get(key);
                    Variable toc = new Variable(key, value);
                    if (!vars.contains(toc)) {
                        vars.add(toc);
                        add(toc);
                        toc.repaint();
                    }
                }
                prev[0] = GraphPanel.VARIABLES;
            }
        });
    }
}
