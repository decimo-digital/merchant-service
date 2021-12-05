package it.decimo.merchant_service.repository;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import it.decimo.merchant_service.model.Merchant;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectMapper mapper;

    /**
     * Recupera tutti i merchant con i relativi metadati di occupazione attuale
     */
    public List<Merchant> findAllMerchantsWithMetadata() {
        final var sql = "select *,((occupied / data.total_seats::float) * 100)::numeric(3, 2) as occupancy_rate from (select merchant.id, merchant.owner, merchant.store_name, merchant.location, md.openings, md.total_seats, md.description, (case when currently_prenotated is null then md.total_seats else md.total_seats - currently_prenotated end) as free_seats, (case when currently_prenotated is null then 0 else currently_prenotated end) as occupied from merchant left join (select merchant, count(validity.valid) as currently_prenotated from prenotation p join (select prenotation.id, (case when extract(hour from validity.age) <= 1 and extract(minute from validity.age) <= 30 then true else false end) as valid from prenotation join(select id, age(now(), to_timestamp(date_millis / 1000)) as age from prenotation) as validity on validity.id = prenotation.id order by date desc) validity on p.id = validity.id where validity.valid = true group by merchant) as prenotations on id = prenotations.merchant join merchant_data md on merchant.id = md.merchant_id) as data;";

        final var map = jdbcTemplate.queryForList(sql);

        final var merchants = new ArrayList<Merchant>();

        map.forEach(item -> {
            try {
                merchants.add(mapper.readValue(mapper.writeValueAsString(item), Merchant.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return merchants;
    }

    /**
     * Ritorna i dati del merchant specificato
     */
    public Merchant getMerchantData(int merchantId) {
        final var sql = String.format(
                "select *,((occupied / data.total_seats::float) * 100)::numeric(3, 2) as occupancy_rate from ( select merchant.id, merchant.owner, merchant.store_name, merchant.location, md.openings, md.total_seats, md.description, (case when currently_prenotated is null then md.total_seats else md.total_seats - currently_prenotated end) as free_seats, (case when currently_prenotated is null then 0 else currently_prenotated end) as occupied from merchant left join (select merchant, count(validity.valid) as currently_prenotated from prenotation p join (select prenotation.id, (case when extract(hour from validity.age) <= 1 and extract(minute from validity.age) <= 30 then true else false end) as valid from prenotation join(select id, age(now(), to_timestamp(date_millis / 1000)) as age from prenotation) as validity on validity.id = prenotation.id order by date desc) validity on p.id = validity.id where validity.valid = true group by merchant) as prenotations on id = prenotations.merchant join merchant_data md on merchant.id = md.merchant_id where merchant.id = %d) as data",
                merchantId);

        final var map = jdbcTemplate.queryForList(sql);

        if (map.size() != 1) {
            log.error("Got an unexpected amount of merchants", map.size());
            throw new RuntimeException("Got an unexpected amount of merchants");
        }

        try {
            return mapper.readValue(mapper.writeValueAsString(map.get(0)), Merchant.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
