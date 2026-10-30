package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id", nullable = false)
    private int userId;

    @Column(name = "client_name", nullable = false, unique = true, length = 50)
    private String clientName;

    @Column(name = "client_password", nullable = false, length = 100)
    private String clientPassword;

    @Column(name = "client_role", nullable = false, length = 20)
    private String clientRole = "standard";

    @OneToMany(mappedBy = "createdBy")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Video> videos;

    public Client(String username, String password) {
        this.clientName = username;
        this.clientPassword = password;
        this.clientRole = "STANDARD";
    }
}