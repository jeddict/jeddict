//added by gaurav gupta
package org.netbeans.orm.converter.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import static org.netbeans.jcode.jpa.JPAConstants.COLLECTION_TABLE;
import static org.netbeans.jcode.jpa.JPAConstants.COLLECTION_TABLE_FQN;
import org.netbeans.orm.converter.util.ImportSet;
import org.netbeans.orm.converter.util.ORMConverterUtil;

public class CollectionTableSnippet implements Snippet {

    private String catalog = null;
    private String name = null;
    private String schema = null;

    private List<JoinColumnSnippet> joinColumns = Collections.EMPTY_LIST;

    private List<UniqueConstraintSnippet> uniqueConstraints = Collections.EMPTY_LIST;
    private List<IndexSnippet> indices = Collections.EMPTY_LIST;
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

    public List<UniqueConstraintSnippet> getUniqueConstraints() {
        return uniqueConstraints;
    }

    public void setUniqueConstraints(List<UniqueConstraintSnippet> uniqueConstraints) {
        this.uniqueConstraints = uniqueConstraints;
    }

    public boolean isEmpty() {
        return (catalog == null || catalog.trim().isEmpty())
                && (name == null || name.trim().isEmpty())
                && (schema == null || schema.trim().isEmpty())
                && joinColumns.isEmpty()
                && uniqueConstraints.isEmpty() && indices.isEmpty() && foreignKey == null;
    }

    @Override
    public String getSnippet() throws InvalidDataException {

        if (isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@").append(COLLECTION_TABLE).append("(");

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

        if (!uniqueConstraints.isEmpty()) {
            builder.append("uniqueConstraints={");

            for (UniqueConstraintSnippet uniqueConstraint : uniqueConstraints) {
                builder.append(uniqueConstraint.getSnippet());
                builder.append(ORMConverterUtil.COMMA);
            }

            builder.deleteCharAt(builder.length() - 1);
            builder.append(ORMConverterUtil.CLOSE_BRACES);
            builder.append(ORMConverterUtil.COMMA);
        }
        
        if (!indices.isEmpty()) {
            builder.append("indexes={");

            for (IndexSnippet snippet : indices) {
                builder.append(snippet.getSnippet());
                builder.append(ORMConverterUtil.COMMA);
            }

            builder.deleteCharAt(builder.length() - 1);
            builder.append(ORMConverterUtil.CLOSE_BRACES);
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

        if (joinColumns.isEmpty() && uniqueConstraints == null && foreignKey == null) {
            return Collections.singletonList(COLLECTION_TABLE_FQN);
        }

        ImportSet importSnippets = new ImportSet();

        importSnippets.add(COLLECTION_TABLE_FQN);

        if (!joinColumns.isEmpty()) {
            for (JoinColumnSnippet joinColumn : joinColumns) {
                importSnippets.addAll(joinColumn.getImportSnippets());
            }
        }
        
        if (foreignKey != null) {
            importSnippets.addAll(foreignKey.getImportSnippets());
        }

        if (!uniqueConstraints.isEmpty()) {
            importSnippets.addAll(uniqueConstraints.get(0).getImportSnippets());
        }
        
        if (!indices.isEmpty()) {
            importSnippets.addAll(indices.get(0).getImportSnippets());
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

    /**
     * @return the indices
     */
    public List<IndexSnippet> getIndices() {
        return indices;
    }

    /**
     * @param indices the indices to set
     */
    public void setIndices(List<IndexSnippet> indices) {
        this.indices = indices;
    }
}
