package com.atguigu.yygh.hosp.testmongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * title:
 *
 * @Author xu
 * @Date 2022/07/20 15:31
 * @FileName: TestMongo2
 */
@RestController
@RequestMapping("/mongo2")
public class TestMongo2 {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PerRepository perRepository;

    //SpringData方法规范
    @GetMapping("testMethod2")
    public void testMethod2() {
        List users = userRepository.getByNameLike("三");
        users.forEach(System.out::println);
    }

    //SpringData方法规范
    @GetMapping("testMethod1")
    public void testMethod1() {
        List users = userRepository.getByNameAndAge("张三", 20);
        users.forEach(System.out::println);
    }

    //删除
    @GetMapping("delete")
    public void delete() {
        userRepository.deleteById("62d7b58391fe621f90684636");
    }

    //修改
    @GetMapping("update")
    public void updateUser() {
        User user = userRepository.findById("62d7b58391fe621f90684636").get();
        user.setName("张三_1");
        user.setAge(25);
        user.setEmail("883220990@qq.com");
        User save = userRepository.save(user);
        System.out.println(save);
    }

    //模糊分页查询
    @GetMapping("findPage")
    public void findUsersPage() {
        Sort sort = Sort.by(Sort.Direction.DESC, "age");
        //Pageable的第一页从0开始
        Pageable pageable = PageRequest.of(0, 10, sort);
        User user = new User();
        user.setName("三");

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        Example<User> example = Example.of(user, matcher);
        Page<User> userPage = userRepository.findAll(example, pageable);
        System.out.println(userPage.getTotalElements());
        userPage.getContent().forEach(System.out::println);
    }

    //模糊查询
    @GetMapping("findLike")
    public void findUsersLikeName() {
        User user = new User();
        user.setName("三");

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        Example<User> example = Example.of(user, matcher);
        List<User> users = userRepository.findAll(example);
        users.forEach(System.out::println);
    }

    //条件查询
    @GetMapping("findUser")
    public void findUserList() {
        User user = new User();
        user.setName("张三");
        user.setAge(20);
        Example<User> example = Example.of(user);
        List<User> users = userRepository.findAll(example);
        users.forEach(System.out::println);
    }

    //根据id查询
    @GetMapping("findById")
    public void findById() {
        User user = userRepository.findById("62d7a40161f9f558da014776").get();
        System.out.println("user = " + user);
        //--------------------------------
        Per per = perRepository.findById("62d7ee3e07f8d470c20fcf66").get();
        System.out.println("per = " + per);
    }

    //查询所有
    @GetMapping("findAll")
    public void findAll() {
        List<User> users = userRepository.findAll();
        users.forEach(System.out::println);
        //--------------------------------
        List<Per> perList = perRepository.findAll();
        perList.forEach(System.out::println);
    }

    //新增
    @GetMapping("create")
    public void create() {
        User user = new User();
        user.setAge(20);
        user.setName("张三");
        user.setEmail("3332200@qq.com");
        User user1 = userRepository.save(user);
        System.out.println("user1 = " + user1);
    }
}
