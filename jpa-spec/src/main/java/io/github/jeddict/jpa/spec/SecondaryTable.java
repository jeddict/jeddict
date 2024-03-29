//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2014.01.21 at 01:52:19 PM IST
//
package io.github.jeddict.jpa.spec;

import io.github.jeddict.jpa.spec.validator.column.ForeignKeyValidator;
import io.github.jeddict.source.AnnotatedMember;
import io.github.jeddict.source.AnnotationExplorer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyForeignKeyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.SecondaryTableMetadata;

/**
 *
 *
 * @Target({TYPE}) @Retention(RUNTIME) public @interface SecondaryTable { String
 * name(); String catalog() default ""; String schema() default "";
 * PrimaryKeyJoinColumn[] pkJoinColumns() default {}; UniqueConstraint[]
 * uniqueConstraints() default {}; Index[] indexes() default {}; }
 *
 *
 *
 * <p>
 * Java class for secondary-table complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="secondary-table">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;sequence>
 *           &lt;element name="primary-key-join-column" type="{http://xmlns.jcp.org/xml/ns/persistence/orm}primary-key-join-column" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element name="primary-key-foreign-key" type="{http://xmlns.jcp.org/xml/ns/persistence/orm}foreign-key" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;element name="unique-constraint" type="{http://xmlns.jcp.org/xml/ns/persistence/orm}unique-constraint" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="index" type="{http://xmlns.jcp.org/xml/ns/persistence/orm}index" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="catalog" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="schema" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "secondary-table", propOrder = {
    "primaryKeyJoinColumn",
    "primaryKeyForeignKey",
    "foreignKey"
})
public class SecondaryTable extends Table {

    @XmlElement(name = "pk-jc")
    protected List<PrimaryKeyJoinColumn> primaryKeyJoinColumn;
    @XmlElement(name = "pk-fk")
    protected ForeignKey primaryKeyForeignKey;//REVENG PENDING
    @XmlElement(name = "fk")
    protected ForeignKey foreignKey;//REVENG PENDING

    public static SecondaryTable load(AnnotationExplorer annotation) {
        SecondaryTable secondaryTable = new SecondaryTable();

        annotation.getString("name").ifPresent(secondaryTable::setName);
        annotation.getString("catalog").ifPresent(secondaryTable::setCatalog);
        annotation.getString("schema").ifPresent(secondaryTable::setSchema);

        secondaryTable.uniqueConstraint
                = annotation.getAnnotationList("uniqueConstraints")
                        .map(UniqueConstraint::load)
                        .collect(toCollection(LinkedHashSet::new));

        secondaryTable.index
                = annotation.getAnnotationList("indexes")
                        .map(Index::load)
                        .collect(toList());

        return secondaryTable;
    }

    public static List<SecondaryTable> load(AnnotatedMember member) {
        List<SecondaryTable> secondaryTables = new ArrayList<>();
        Optional<AnnotationExplorer> secondaryTablesOpt = member.getAnnotation(jakarta.persistence.SecondaryTables.class);
        if (secondaryTablesOpt.isPresent()) {
            secondaryTables.addAll(
                    secondaryTablesOpt.get()
                            .getAnnotationList("value")
                            .map(SecondaryTable::load)
                            .collect(toList())
            );
        }

        secondaryTables.addAll(
                member.getRepeatableAnnotations(jakarta.persistence.SecondaryTable.class)
                        .map(SecondaryTable::load)
                        .collect(toList())
        );
        return secondaryTables;
    }



    /**
     * Gets the value of the primaryKeyJoinColumn property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the primaryKeyJoinColumn property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrimaryKeyJoinColumn().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PrimaryKeyJoinColumn }
     *
     *
     */
    public List<PrimaryKeyJoinColumn> getPrimaryKeyJoinColumn() {
        if (primaryKeyJoinColumn == null) {
            primaryKeyJoinColumn = new ArrayList<>();
        }
        return this.primaryKeyJoinColumn;
    }

    /**
     * Gets the value of the primaryKeyForeignKey property.
     *
     * @return possible object is {@link ForeignKey }
     *
     */
    public ForeignKey getPrimaryKeyForeignKey() {
        return primaryKeyForeignKey;
    }

    /**
     * Sets the value of the primaryKeyForeignKey property.
     *
     * @param value allowed object is {@link ForeignKey }
     *
     */
    public void setPrimaryKeyForeignKey(ForeignKey value) {
        this.primaryKeyForeignKey = value;
    }
    
    /**
     * Gets the value of the foreignKey property.
     *
     * @return possible object is {@link ForeignKey }
     *
     */
    public ForeignKey getForeignKey() {
        if(foreignKey==null){
            foreignKey = new ForeignKey();
        }
        return foreignKey;
    }

    /**
     * Sets the value of the foreignKey property.
     *
     * @param value allowed object is {@link ForeignKey }
     *
     */
    public void setForeignKey(ForeignKey value) {
        this.foreignKey = value;
    }

    @Override
    public SecondaryTableMetadata getAccessor() {
        SecondaryTableMetadata accessor = new SecondaryTableMetadata();
        super.getAccessor(accessor);
        accessor.setPrimaryKeyJoinColumns(this.getPrimaryKeyJoinColumn()
                .stream()
                .map(PrimaryKeyJoinColumn::getAccessor)
                .collect(toList()));
        if (ForeignKeyValidator.isNotEmpty(primaryKeyForeignKey)) {
            accessor.setPrimaryKeyForeignKey((PrimaryKeyForeignKeyMetadata)primaryKeyForeignKey.getAccessor(new PrimaryKeyForeignKeyMetadata()));
        }
        return accessor;
    }

}
