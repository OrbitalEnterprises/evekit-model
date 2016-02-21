package enterprises.orbital.evekit.model.character;

import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AbstractModelTester;

public class CharacterSkillInTrainingTest extends AbstractModelTester<CharacterSkillInTraining> {
  final boolean                                             skillInTraining          = true;
  final long                                                currentTrainingQueueTime = TestBase.getRandomInt(100000000);
  final long                                                trainingStartTime        = TestBase.getRandomInt(100000000);
  final long                                                trainingEndTime          = TestBase.getRandomInt(100000000);
  final int                                                 trainingStartSP          = TestBase.getRandomInt(100000000);
  final int                                                 trainingDestinationSP    = TestBase.getRandomInt(100000000);
  final int                                                 trainingToLevel          = TestBase.getRandomInt(100000000);
  final int                                                 skillTypeID              = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<CharacterSkillInTraining> eol                      = new ClassUnderTestConstructor<CharacterSkillInTraining>() {

                                                                                       @Override
                                                                                       public CharacterSkillInTraining getCUT() {
                                                                                         return new CharacterSkillInTraining(
                                                                                             skillInTraining, currentTrainingQueueTime, trainingStartTime,
                                                                                             trainingEndTime, trainingStartSP, trainingDestinationSP,
                                                                                             trainingToLevel, skillTypeID);
                                                                                       }

                                                                                     };

  final ClassUnderTestConstructor<CharacterSkillInTraining> live                     = new ClassUnderTestConstructor<CharacterSkillInTraining>() {
                                                                                       @Override
                                                                                       public CharacterSkillInTraining getCUT() {
                                                                                         return new CharacterSkillInTraining(
                                                                                             skillInTraining, currentTrainingQueueTime + 1, trainingStartTime,
                                                                                             trainingEndTime, trainingStartSP, trainingDestinationSP,
                                                                                             trainingToLevel, skillTypeID);
                                                                                       }

                                                                                     };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CharacterSkillInTraining>() {

      @Override
      public CharacterSkillInTraining[] getVariants() {
        return new CharacterSkillInTraining[] {
            new CharacterSkillInTraining(
                !skillInTraining, currentTrainingQueueTime, trainingStartTime, trainingEndTime, trainingStartSP, trainingDestinationSP, trainingToLevel,
                skillTypeID),
            new CharacterSkillInTraining(
                skillInTraining, currentTrainingQueueTime + 1, trainingStartTime, trainingEndTime, trainingStartSP, trainingDestinationSP, trainingToLevel,
                skillTypeID),
            new CharacterSkillInTraining(
                skillInTraining, currentTrainingQueueTime, trainingStartTime + 1, trainingEndTime, trainingStartSP, trainingDestinationSP, trainingToLevel,
                skillTypeID),
            new CharacterSkillInTraining(
                skillInTraining, currentTrainingQueueTime, trainingStartTime, trainingEndTime + 1, trainingStartSP, trainingDestinationSP, trainingToLevel,
                skillTypeID),
            new CharacterSkillInTraining(
                skillInTraining, currentTrainingQueueTime, trainingStartTime, trainingEndTime, trainingStartSP + 1, trainingDestinationSP, trainingToLevel,
                skillTypeID),
            new CharacterSkillInTraining(
                skillInTraining, currentTrainingQueueTime, trainingStartTime, trainingEndTime, trainingStartSP, trainingDestinationSP + 1, trainingToLevel,
                skillTypeID),
            new CharacterSkillInTraining(
                skillInTraining, currentTrainingQueueTime, trainingStartTime, trainingEndTime, trainingStartSP, trainingDestinationSP, trainingToLevel + 1,
                skillTypeID),
            new CharacterSkillInTraining(
                skillInTraining, currentTrainingQueueTime, trainingStartTime, trainingEndTime, trainingStartSP, trainingDestinationSP, trainingToLevel,
                skillTypeID + 1)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_SKILL_IN_TRAINING));

  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<CharacterSkillInTraining>() {

      @Override
      public CharacterSkillInTraining getModel(SynchronizedEveAccount account, long time) {
        return CharacterSkillInTraining.get(account, time);
      }

    });
  }
}
