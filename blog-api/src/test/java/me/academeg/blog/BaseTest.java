package me.academeg.blog;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 25.03.2017
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
//@Transactional
//@Rollback
public abstract class BaseTest {
}
