package boot;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductInfoRepository {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private DynamoDBMapper dbMapper;

  public List<ProductInfo> readAll() {
    log.trace("Entering readAll()");
    PaginatedList<ProductInfo> results = dbMapper.scan(
    	ProductInfo.class, new DynamoDBScanExpression());
    results.loadAllResults();
    return results;
  }

  public Optional<ProductInfo> read(String id) {
    log.trace("Entering read() with {}", id);
    return Optional.ofNullable(dbMapper.load(ProductInfo.class, id));
  }

  public void save(ProductInfo productInfo) {
    log.trace("Entering save() with {}", productInfo);
    dbMapper.save(productInfo);
  }

  public void delete(String name) {
  }
}