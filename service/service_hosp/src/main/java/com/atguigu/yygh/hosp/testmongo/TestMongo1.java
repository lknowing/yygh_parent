package com.atguigu.yygh.hosp.testmongo;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.regex.Pattern;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/20 14:38
 * @FileName: TestMongo1
 */
@RestController
@RequestMapping("/mongo1")
public class TestMongo1 {
    @Autowired
    private MongoTemplate mongoTemplate;

    //删除操作
    @GetMapping("delete")
    public void delete() {
        Query query =
                new Query(Criteria.where("_id").is("62d7a40161f9f558da014776"));
        DeleteResult result = mongoTemplate.remove(query, User.class);
        long count = result.getDeletedCount();
        System.out.println(count);
    }

    //修改
    @GetMapping("update")
    public void updateUser() {
        User user = mongoTemplate.findById("62d7a40161f9f558da014776", User.class);
        user.setName("test_1");
        user.setAge(25);
        user.setEmail("493220990@qq.com");
        Query query = new Query(Criteria.where("_id").is(user.getId()));
        Update update = new Update();
        update.set("name", user.getName());
        update.set("age", user.getAge());
        update.set("email", user.getEmail());
        UpdateResult result = mongoTemplate.upsert(query, update, User.class);
        long count = result.getModifiedCount();
        System.out.println(count);
    }

    //模糊分页查询
    @GetMapping("findPage")
    public void findUsersPage() {
//        String name = "est";
//        String regex = String.format("%s%s%s", "^.*", name, ".*$");
        String name = "Li";
        String regex = String.format("%s%s%s", "^.*", name, ".*$");
        int pageNo = 1;
        int pageSize = 10;
        //模糊查询
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Query query = new Query(Criteria.where("name").regex(pattern));
        //总记录数
        //long total = mongoTemplate.count(query, User.class);
        long total = mongoTemplate.count(query, Per.class);
        //分页条件设置
        query.skip((pageNo - 1) * pageSize).limit(pageSize);

        //List<User> userList = mongoTemplate.find(query, User.class);
        List<Per> perList = mongoTemplate.find(query, Per.class);
        //userList.forEach(System.out::println);
        perList.forEach(System.out::println);
        System.out.println("total = " + total);
    }

    //模糊查询
    @GetMapping("findLike")
    public void findUsersLikeName() {
        String name = "Li";
        String regex = String.format("%s%s%s", "^.*", name, ".*$");
        //compile()方法：将给定的正则表达式(regex)编译为具有给定标志(Pattern.CASE_INSENSITIVE)的模式，返回值是Pattern。
        //CASE_INSENSITIVE是启用不区分大小写
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Query query = new Query(Criteria
                .where("name").regex(pattern));
        List<Per> perList = mongoTemplate.find(query, Per.class);
        perList.forEach(System.out::println);
        //Per(id=62d7de2aa93e4fa72212740c, name=LiKui, age=22, sex=true, score=90)
        //Per(id=62d7de3fa93e4fa72212740d, name=LinChong, age=26, sex=true, score=95)
//        String name = "est";
//        String regex = String.format("%s%s%s", "^.*", name, ".*$");
//        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
//        Query query = new Query(Criteria.where("name").regex(pattern));
//        List<User> userList = mongoTemplate.find(query, User.class);
//        userList.forEach(System.out::println);
    }

    //条件查询,严格匹配
    @GetMapping("findUser")
    public void findUserList() {
        Query query = new Query(Criteria
                .where("name").is("WuSong")
                .and("age").is(31));
        List<Per> perList = mongoTemplate.find(query, Per.class);
        perList.forEach(System.out::println);//Per(id=62d7e43383e64bfa0fafb596, name=WuSong, age=31, sex=null, score=85)
//        Query query = new Query(Criteria.where("name").is("test").and("age").is(20));
//        List<User> userList = mongoTemplate.find(query, User.class);
//        userList.forEach(System.out::println);
    }

    //根据id查询
    @GetMapping("findById")
    public void findById() {
        Per per = mongoTemplate.findById("62d7d839a93e4fa722127409", Per.class);
        System.out.println("per = " + per);//per = Per(id=62d7d839a93e4fa722127409, name=SongJiang, age=30, sex=true, score=60)
//        User user = mongoTemplate.findById("62d7a40161f9f558da014776", User.class);
//        System.out.println("user = " + user);
    }

    //查询所有
    @GetMapping("findAll")
    public void findAll() {
        List<Per> perList = mongoTemplate.findAll(Per.class);
        perList.forEach(System.out::println);
        //Per(id=62d7d839a93e4fa722127409, name=SongJiang, age=30, sex=true, score=60)
        //Per(id=62d7d8c0a93e4fa72212740a, name=WuYong, age=27, sex=true, score=98)
        //...
//        List<User> users = mongoTemplate.findAll(User.class);
//        users.forEach(System.out::println);
    }

    //新增
    @GetMapping("create")
    public void create() {
        Per per = new Per(null, "HuaRong", 25, true, 91);
        Per per1 = mongoTemplate.insert(per);
        System.out.println("per1 = " + per1);//per1 = Per(id=62d7ee3e07f8d470c20fcf66, name=HuaRong, age=25, sex=true, score=91)
//        User user = new User();
//        user.setAge(20);
//        user.setName("test");
//        user.setEmail("4932200@qq.com");
//        User user1 = mongoTemplate.insert(user);
//        System.out.println("user1 = " + user1);
    }
}
