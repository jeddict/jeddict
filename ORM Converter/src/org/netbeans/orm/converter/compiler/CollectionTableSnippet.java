//added by gaurav gupta
package org.netbeans.orm.converter.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.netbeans.orm.converter.util.ORMConverterUtil;

public class CollectionTableSnippet implements Snippet {

    private String catalog = null;
    private String name = null;
    private String schema = null;

    private List<JoinColumnSnippet> joinColumns = Collections.EMPTY_LIST;

    private UniqueConstraintSnippet uniqueConstraint = null;
    private ForeignKeySnippet foreignKey;

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public List<JoinColumnSnippet> getJoinColumns() {
        return joinColumns;
    }

    public void setJoinColumns(List<JoinColumnSnippet> joinColumns) {
        if (joinColumns != null) {
            this.joinColumns = joinColumns;
        }
    }

    public UniqueConstraintSnippet getUniqueConstraint() {
        return uniqueConstraint;
    }

    public void setUniqueConstraints(UniqueConstraintSnippet uniqueConstraint) {
        this.uniqueConstraint = uniqueConstraint;
    }

    public boolean isEmpty() {
        return (catalog == null || catalog.trim().isEmpty())
                && (name == null || name.trim().isEmpty())
                && (schema == null || schema.trim().isEmpty())
                && joinColumns.isEmpty()
                && uniqueConstraint == null;
    }

    @Override
    public String getSnippet() throws InvalidDataException {

        if ((catalog == null || catalog.trim().isEmpty())
                && (name == null || name.trim().isEmpty())
                && (schema == null || schema.trim().isEmpty())
                && joinColumns.isEmpty()
                && uniqueConstraint == null && foreignKey == null) {
            return null;//"@CollectionTable";
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@CollectionTable(");

        if (name != null && !name.trim().isEmpty()) {
            builder.append("name=\"");
            builder.append(name);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (schema != null && !schema.trim().isEmpty()) {
            builder.append("schema=\"");
            builder.append(schema);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (catalog != null && !catalog.trim().isEmpty()) {
            builder.append("catalog=\"");
            builder.append(catalog);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (uniqueConstraint != null) {
            builder.append("uniqueConstraints=");

            builder.append(uniqueConstraint.getSnippet());

            builder.append(ORMConverterUtil.COMMA);
        }

        if (!joinColumns.isEmpty()) {
            builder.append("joinColumns={");

            for (JoinColumnSnippet joinColumn : joinColumns) {
                builder.append(joinColumn.getSnippet());
                builder.append(ORMConverterUtil.COMMA);
            }

            builder.deleteCharAt(builder.length() - 1);

            builder.append(ORMConverterUtil.CLOSE_BRACES);
            builder.append(ORMConverterUtil.COMMA);
        }
        
        if (foreignKey != null) {
            builder.append("foreignKey=");
            builder.append(foreignKey.getSnippet());
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (isEmpty()) {
            return new ArrayList<>();
        }

        if (joinColumns.isEmpty() && uniqueConstraint == null && foreignKey == null) {
            return Collections.singletonList("javax.persistence.CollectionTable");
        }

        Collection<String> importSnippets = new ArrayList<>();

        importSnippets.add("javax.persistence.CollectionTable");

        if (!joinColumns.isEmpty()) {
            Collection<String> joinColumnSnippets  = joinColumns.get(0).getImportSnippets();
            importSnippets.addAll(joinColumnSnippets);
        }
        
        if (foreignKey != null) {
            importSnippets.addAll(foreignKey.getImportSnippets());
        }

        if (uniqueConstraint != null) {
            importSnippets.addAll(uniqueConstraint.getImportSnippets());
        }

        return importSnippets;
    }

    /**
     * @return the foreignKey
     */
    public ForeignKeySnippet getForeignKey() {
        return foreignKey;
    }

    /**
     * @param foreignKey the foreignKey to set
     */
    public void setForeignKey(ForeignKeySnippet foreignKey) {
        this.foreignKey = foreignKey;
    }
}
