@import com.example.demo.Entities.User
@import java.util.List
@param List<User> users

<!DOCTYPE HTML>

<html lang="en">
<head>
    <title>demo site</title>
    <meta charset="utf-8" />
</head>
<body>

<div id="user-list" class="container">

    @for(User user : users)
        <ul>
            ${user.getAccountId()}. ${user.getFirstName()} ${user.getLastName()}
        </ul>
    @endfor

</div>

</body>
</html>