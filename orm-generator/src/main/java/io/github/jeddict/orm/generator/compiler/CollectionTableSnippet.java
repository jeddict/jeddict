//added by gaurav gupta
package io.github.jeddict.orm.generator.compiler;

import static io.github.jeddict.jcode.JPAConstants.COLLECTION_TABLE;
import static io.github.jeddict.jcode.JPAConstants.COLLECTION_TABLE_FQN;
import io.github.jeddict.orm.generator.util.ImportSet;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.singletonList;
import java.util.List;

public class CollectionTableSnippet implements Snippet {

    private String catalog = null;
    private String name = null;
    private String schema = null;

    private List<JoinColumnSnippet> joinColumns = Collections.<JoinColumnSnippet>emptyList();

    private List<UniqueConstraintSnippet> uniqueConstraints = Collections.<UniqueConstraintSnippet>emptyList();
    private List<IndexSnippet> indices = Collections.<IndexSnippet>emptyList();
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
        builder.append(AT).append(COLLECTION_TABLE).append(OPEN_PARANTHESES);

        builder.append(buildString("name", name));
        builder.append(buildString("schema", schema));
        builder.append(buildString("catalog", catalog));

        builder.append(buildAnnotations("uniqueConstraints", uniqueConstraints));
        builder.append(buildAnnotations("indexes", indices));
        builder.append(buildAnnotations("joinColumns", joinColumns));

        builder.append(buildAnnotation("foreignKey", foreignKey));

        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (isEmpty()) {
            return new ArrayList<>();
        }

        if (joinColumns.isEmpty() && uniqueConstraints == null && foreignKey == null) {
            return singletonList(COLLECTION_TABLE_FQN);
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
