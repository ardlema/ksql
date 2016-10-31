package io.confluent.ksql.planner.plan;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.confluent.ksql.metastore.DataSource;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;

import java.util.Arrays;
import java.util.List;

public class JoinNode extends PlanNode {

    private final PlanNode left;
    private final PlanNode right;
    private final Schema schema;
    private final String leftKeyFieldName;
    private final String rightKeyFieldName;

    private final String leftAlias;
    private final String rightAlias;

    public JoinNode(@JsonProperty("id") PlanNodeId id,
                    @JsonProperty("left") PlanNode left,
                    @JsonProperty("right") PlanNode right,
                    @JsonProperty("leftKeyFieldName") String leftKeyFieldName,
                    @JsonProperty("rightKeyFieldName") String rightKeyFieldName,
                    @JsonProperty("leftAlias") String leftAlias,
                    @JsonProperty("rightAlias") String rightAlias) {

        // TODO: Type should be derived.
        super(id);
        this.left = left;
        this.right = right;
        this.leftKeyFieldName =leftKeyFieldName;
        this.rightKeyFieldName = rightKeyFieldName;
        this.leftAlias = leftAlias;
        this.rightAlias = rightAlias;
        this.schema = buildSchema(left, right);
    }

    private Schema buildSchema(PlanNode left, PlanNode right) {

        Schema leftSchema = left.getSchema();
        Schema rightSchema = right.getSchema();

        SchemaBuilder schemaBuilder = SchemaBuilder.struct();

        for (Field field: leftSchema.fields()) {
            String fieldName = leftAlias+"."+field.name();
            schemaBuilder.field(fieldName, field.schema());
        }

        for (Field field: rightSchema.fields()) {
            String fieldName = rightAlias+"."+field.name();
            schemaBuilder.field(fieldName, field.schema());
        }
        return schemaBuilder.build();
    }

    @Override
    public Schema getSchema() {
        return this.schema;
    }

    @Override
    public Field getKeyField() {
        return this.left.getKeyField();
    }

    @Override
    public List<PlanNode> getSources() {
        return Arrays.asList(left, right);
    }

    public PlanNode getLeft() {
        return left;
    }

    public PlanNode getRight() {
        return right;
    }

    public String getLeftKeyFieldName() {
        return leftKeyFieldName;
    }

    public String getRightKeyFieldName() {
        return rightKeyFieldName;
    }

    public String getLeftAlias() {
        return leftAlias;
    }

    public String getRightAlias() {
        return rightAlias;
    }
}
