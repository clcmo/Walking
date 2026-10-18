# CI/CD

Este projeto é desenvolvido em Kotlin. O pipeline de integração e entrega contínuas deve validar cada alteração antes da integração e publicar artefatos somente após a aprovação das verificações.

## Ambiente

- Use a versão do JDK definida pelo projeto, conforme `build.gradle.kts` ou `gradle/libs.versions.toml`.
- Execute o Gradle pelo Wrapper (`./gradlew`); mantenha o Wrapper versionado no repositório.
- Em projetos Android, configure também o Android SDK e as ferramentas necessárias no executor.

## Verificações

Na raiz do repositório, execute:

```bash
./gradlew clean build
```

O pipeline deve executar esse comando em pull requests e em pushes para a branch principal. Ele compila o projeto e executa as tarefas de verificação configuradas, incluindo testes. Publique os relatórios de teste como artefatos do job, mesmo quando houver falhas.

## Configuração do CI

Um workflow deve, no mínimo:

1. Fazer checkout do código.
2. Configurar o JDK compatível com o projeto.
3. Configurar o Gradle Wrapper e habilitar cache de dependências.
4. Executar `./gradlew clean build`.
5. Preservar os relatórios de testes e compilação para diagnóstico.

No GitHub Actions, isso pode ser implementado com `actions/checkout`, `actions/setup-java` e `gradle/actions/setup-gradle`. Fixe as versões das actions conforme a política do repositório e alinhe a versão do JDK à configuração do projeto.

## Entrega e publicação

Execute o CD apenas depois que o CI passar. Gere e publique o artefato usando as tarefas Gradle configuradas para o projeto. Bibliotecas Kotlin/JVM podem publicar os artefatos definidos pelo plugin Gradle; aplicações Android podem gerar APK ou AAB. Não presuma um destino de publicação: configure-o conforme a plataforma adotada pelo projeto.

Armazene tokens, chaves e credenciais nos secrets do provedor de CI, nunca no código versionado. Para releases, use tags ou aprovação explícita, associe a publicação ao commit correspondente e evite substituir artefatos já publicados.

## Boas práticas

- Mantenha JDK, Kotlin e Gradle compatíveis entre desenvolvimento e CI.
- Não publique se compilação ou testes falharem.
- Restrinja as permissões do workflow ao mínimo necessário e não exponha secrets em builds de forks.
