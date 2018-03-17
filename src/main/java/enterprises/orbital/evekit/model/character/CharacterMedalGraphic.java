package enterprises.orbital.evekit.model.character;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_data_character_medal_graphic",
    indexes = {
        @Index(
            name = "medalIDGraphicIndex",
            columnList = "medalID"),
        @Index(
            name = "issuedGraphicIndex",
            columnList = "issued")
    })
@NamedQueries({
    @NamedQuery(
        name = "CharacterMedalGraphic.getbyID",
        query = "SELECT c FROM CharacterMedalGraphic c where c.owner = :owner and c.medalID = :mid and c.issued = :issued and c.part = :part and c.layer = :layer and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class CharacterMedalGraphic extends CachedData {
  private static final Logger log = Logger.getLogger(CharacterMedalGraphic.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEDALS);

  private int medalID;
  private long issued = -1;
  private int part;
  private int layer;
  private String graphic;
  private int color;

  @Transient
  @ApiModelProperty(
      value = "issued Date")
  @JsonProperty("issuedDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date issuedDate;

  @SuppressWarnings("unused")
  protected CharacterMedalGraphic() {}

  public CharacterMedalGraphic(int medalID, long issued, int part, int layer, String graphic, int color) {
    this.medalID = medalID;
    this.issued = issued;
    this.part = part;
    this.layer = layer;
    this.graphic = graphic;
    this.color = color;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    issuedDate = assignDateField(issued);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof CharacterMedalGraphic)) return false;
    CharacterMedalGraphic other = (CharacterMedalGraphic) sup;
    return medalID == other.medalID &&
        issued == other.issued &&
        part == other.part &&
        layer == other.layer &&
        nullSafeObjectCompare(graphic, other.graphic) &&
        color == other.color;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getMedalID() {
    return medalID;
  }

  public long getIssued() {
    return issued;
  }

  public int getPart() {
    return part;
  }

  public int getLayer() {
    return layer;
  }

  public String getGraphic() {
    return graphic;
  }

  public int getColor() {
    return color;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CharacterMedalGraphic that = (CharacterMedalGraphic) o;
    return medalID == that.medalID &&
        issued == that.issued &&
        part == that.part &&
        layer == that.layer &&
        color == that.color &&
        Objects.equals(graphic, that.graphic);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), medalID, issued, part, layer, graphic, color);
  }

  @Override
  public String toString() {
    return "CharacterMedalGraphic{" +
        "medalID=" + medalID +
        ", issued=" + issued +
        ", part=" + part +
        ", layer=" + layer +
        ", graphic='" + graphic + '\'' +
        ", color=" + color +
        ", issuedDate=" + issuedDate +
        '}';
  }

  public static CharacterMedalGraphic get(
      final SynchronizedEveAccount owner,
      final long time,
      final int medalID,
      final long issued,
      final int part,
      final int layer) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<CharacterMedalGraphic> getter = EveKitUserAccountProvider.getFactory()
                                                                                                            .getEntityManager()
                                                                                                            .createNamedQuery(
                                                                                                                "CharacterMedalGraphic.getbyID",
                                                                                                                CharacterMedalGraphic.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("mid", medalID);
                                        getter.setParameter("issued", issued);
                                        getter.setParameter("part", part);
                                        getter.setParameter("layer", layer);
                                        getter.setParameter("point", time);
                                        try {
                                          return getter.getSingleResult();
                                        } catch (NoResultException e) {
                                          return null;
                                        }
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

  public static List<CharacterMedalGraphic> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector medalID,
      final AttributeSelector issued,
      final AttributeSelector part,
      final AttributeSelector layer,
      final AttributeSelector graphic,
      final AttributeSelector color) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM CharacterMedalGraphic c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "medalID", medalID);
                                        AttributeSelector.addLongSelector(qs, "c", "issued", issued);
                                        AttributeSelector.addIntSelector(qs, "c", "part", part);
                                        AttributeSelector.addIntSelector(qs, "c", "layer", layer);
                                        AttributeSelector.addStringSelector(qs, "c", "graphic", graphic, p);
                                        AttributeSelector.addIntSelector(qs, "c", "color", color);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<CharacterMedalGraphic> query = EveKitUserAccountProvider.getFactory()
                                                                                                           .getEntityManager()
                                                                                                           .createQuery(
                                                                                                               qs.toString(),
                                                                                                               CharacterMedalGraphic.class);
                                        query.setParameter("owner", owner);
                                        p.fillParams(query);
                                        query.setMaxResults(maxresults);
                                        return query.getResultList();
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

}
