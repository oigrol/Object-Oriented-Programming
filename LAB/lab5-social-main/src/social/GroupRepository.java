package social;

//mette in relazione la classe e il database, senza che
//la classe si preoccupi delle operazioni di db e query
public class GroupRepository extends GenericRepository<Group, String> {
    public GroupRepository() {
        super(Group.class);
    }
}