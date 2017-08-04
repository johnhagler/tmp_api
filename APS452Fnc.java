/*
***************************************************************
*                                                             *
*                           NOTICE                            *
*                                                             *
*   THIS SOFTWARE IS THE PROPERTY OF AND CONTAINS             *
*   CONFIDENTIAL INFORMATION OF INFOR AND/OR ITS AFFILIATES   *
*   OR SUBSIDIARIES AND SHALL NOT BE DISCLOSED WITHOUT PRIOR  *
*   WRITTEN PERMISSION. LICENSED CUSTOMERS MAY COPY AND       *
*   ADAPT THIS SOFTWARE FOR THEIR OWN USE IN ACCORDANCE WITH  *
*   THE TERMS OF THEIR SOFTWARE LICENSE AGREEMENT.            *
*   ALL OTHER RIGHTS RESERVED.                                *
*                                                             *
*   (c) COPYRIGHT 2013 INFOR.  ALL RIGHTS RESERVED.           *
*   THE WORD AND DESIGN MARKS SET FORTH HEREIN ARE            *
*   TRADEMARKS AND/OR REGISTERED TRADEMARKS OF INFOR          *
*   AND/OR ITS AFFILIATES AND SUBSIDIARIES. ALL RIGHTS        *
*   RESERVED.  ALL OTHER TRADEMARKS LISTED HEREIN ARE         *
*   THE PROPERTY OF THEIR RESPECTIVE OWNERS.                  *
*                                                             *
***************************************************************
*/
package mvx.app.pgm;

import mvx.app.common.*;
import mvx.app.util.*;
import mvx.app.plist.*;
import mvx.app.ds.*;
import mvx.util.*;
import mvx.db.common.FieldSelection;

/*
*Modification area - M3
*Nbr            Date   User id     Description
*99999999999999 999999 XXXXXXXXXX  x
*Modification area - Business partner
*Nbr            Date   User id     Description
*99999999999999 999999 XXXXXXXXXX  x
*Modification area - Customer
*Nbr            Date   User id     Description
*99999999999999 999999 XXXXXXXXXX  x
*/

/**
*<BR><B><FONT SIZE=+2>Fnc: Manage Supplier invoice batch - Reject history</FONT></B><BR><BR>
*
* <PRE>
* Function program APS452Fnc is called by interactive program APS452 to
* mangage FAPIBR - Supplier invoice batch - Reject history.
*
* The following interactions and operations are available:
*
* Interaction maintain
* --------------------
* parameter list: cPXAPS452FncINmaintain (instantiated as pMaintain )
* Interaction used when retrieving a reject history transaction.
* Returns any errors or notifications.
*
*
* Interaction settings
* --------------------
* parameter list: cPXAPS452FncINsettings (instantiated as pSettings )
* Interaction used when setting parameters for APS452 function.
* Returns any errors or notifications.
*
* Returns notification 'XRE0103 - Record does not exist' in step INITIATE if there 
* is no settings record for the responsible. In that case default values are 
* returned for the parameters and a record will be written in step UDPATE.
*

* </PRE>
*/
public class APS452Fnc extends Function
{
   public void movexMain() {
      INIT(); 	
      // Interaction maintain
      // =========================================
      if (pMaintain != null) {
         if (pMaintain.getStep() == cEnumStep.INITIATE) {
            do_maintain_initiate();
         } else if (pMaintain.getStep() == cEnumStep.VALIDATE) {
            do_maintain_validate();
         } else if (pMaintain.getStep() == cEnumStep.UPDATE) {
            do_maintain_update();
         }
      // Interaction settings
      // =========================================
      } else if (pSettings != null) {
         if (pSettings.getStep() == cEnumStep.INITIATE) {
            do_settings_initiate();
         } else if (pSettings.getStep() == cEnumStep.VALIDATE) {
            do_settings_validate();
         } else if (pSettings.getStep() == cEnumStep.UPDATE) {
            do_settings_update();
         }
      } else {
         pListError();
      }
      SETLR();
      return;
   }

   /**
   * Interaction maintain - step INITIATE.
   */
   public void do_maintain_initiate() {
      // Declaration
      boolean found_FAPIBR = false;
      // Get record
      // =========================================
      if (pMaintain.passFAPIBR && pMaintain.getMode() != cEnumMode.ADD) {
         // Move record from calling program
         moveFAPIBR(APIBR, pMaintain.APIBR);
         // Set primary keys
         pMaintain.DIVI.set().moveLeftPad(APIBR.getDIVI());
         pMaintain.INBN.set(APIBR.getINBN());
         pMaintain.TRNO.set(APIBR.getTRNO());
      } else {
         // Validate primary key parameters
         // =========================================
         // Division
         pMaintain.DIVI.validateMANDATORYandConstraints();
         // Invoice batch number
         pMaintain.INBN.validateMANDATORYandConstraints();
         // Transaction number
         if (pMaintain.getMode() == cEnumMode.ADD) {
            // Transaction number is set automatically for ADD mode (just before writing the record)
            pMaintain.TRNO.clearValue();
            pMaintain.TRNO.setOUTAccess();
         } else {
            pMaintain.TRNO.setMANDATORYAccess();
         }
         pMaintain.TRNO.validateMANDATORYandConstraints();
         // Return error messages
         if (pMaintain.messages.existError()) {
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pMaintain.parameters.setAllDISABLEDAccess();
            pMaintain.DIVI.setOUTAccess();
            pMaintain.INBN.setOUTAccess();
            pMaintain.TRNO.setOUTAccess();
            return;
         }
         // Read record
         // =========================================
         APIBR.setCONO(currentCONO);
         APIBR.setDIVI().move(pMaintain.DIVI.get());
         APIBR.setINBN(pMaintain.INBN.get());
         APIBR.setTRNO(pMaintain.TRNO.get());
         found_FAPIBR = APIBR.CHAIN("00", APIBR.getKey("00"));
         if (pMaintain.getMode() == cEnumMode.ADD) {
            // - no check on record already exists since TRNO is retrieved at update.
            // Set default values
            APIBR.clearNOKEY("00");
            maintain_initiate_setDefaults();
         } else if (!found_FAPIBR) {
            // MSGID=WTR3103 Transaction number &1 does not exist
            pMaintain.messages.addError(this.DSPGM, "TRNO", "WTR3103", CRCommon.formatNumForMsg(APIBR.getTRNO()));
         }
         // Return error messages
         if (pMaintain.messages.existError()) {
            // Set key parameters to access mode OUT and other parameters to DISABLED.
            pMaintain.parameters.setAllDISABLEDAccess();
            pMaintain.DIVI.setOUTAccess();
            pMaintain.INBN.setOUTAccess();
            pMaintain.TRNO.setOUTAccess();
            return;
         }
      }
      // !! Only display mode is allowed in this program !!
      if (pMaintain.getMode() != cEnumMode.RETRIEVE) {
         // MSGID=XOP0100 Option &1 is not allowed for this line
         pMaintain.messages.addError(this.DSPGM, "TRNO", "XOP0100", " 5");
      }
      // Division - check access authority
      if (!CRCommon.validateDIVIfinancialAccessAuthority(pMaintain.DIVI.get(), PLCHKAD, MSGID, MSGDTA)) {
         pMaintain.messages.addError(this.DSPGM, "DIVI", MSGID.toStringRTrim(), MSGDTA);
      }
      found_FAPIBH = cRefINBNext.getFAPIBH(APIBH, found_FAPIBH, currentCONO, pMaintain.DIVI.get(), pMaintain.INBN.get());
      if (!found_FAPIBH) {
         // MSGID=WINBN03 Invoice batch number &1 does not exist
         pMaintain.messages.addError(this.DSPGM, "INBN", "WINBN03", CRCommon.formatNumForMsg(APIBH.getINBN()));
      }
      if (APIBH.getSUPA() != cRefSUPAext.NEW() &&
          pMaintain.getMode() != cEnumMode.RETRIEVE) {
         // XOP0200 = Change
         this.MSGDTA.moveLeftPad(formatToString(SRCOMRCM.getMessage("XOP0200", "MVXCON")));
         messageData.catPad(" 2", formatToString(APIBH.getSUPA(), 2));
         this.MSGDTA.setAppendPos(40);
         this.MSGDTA.append(messageData);
         // MSGID=X_00051 &1 (Option &2) cannot be used when status is &3
         pMaintain.messages.addError(this.DSPGM, "INBN", "X_00051", this.MSGDTA);
      }
      if (pMaintain.getMode() != cEnumMode.RETRIEVE) {
         // Check if Invoice batch number is Work in progress
         checkInvoiceForWIP(pMaintain.DIVI.get(), pMaintain.INBN.get(), pMaintain.messages);
      }
      if (pMaintain.messages.existError()) {
         // Set key parameters to access mode OUT and other parameters to DISABLED.
         pMaintain.parameters.setAllDISABLEDAccess();
         pMaintain.DIVI.setOUTAccess();
         pMaintain.INBN.setOUTAccess();
         pMaintain.TRNO.setOUTAccess();
         return;
      }
      // Validate optional/mandatory parameters in ADD mode
      // =========================================
      // - ADD mode not applicable
      // Return error messages
      if (pMaintain.messages.existError()) {
         // Set key/optional/mandatory parameters to access mode OUT
         // and other parameters to DISABLED.
         pMaintain.parameters.setAllDISABLEDAccess();
         pMaintain.DIVI.setOUTAccess();
         pMaintain.INBN.setOUTAccess();
         pMaintain.TRNO.setOUTAccess();
         return;
      }
      // Set parameters
      // =========================================
      maintain_initiate_setParameters();
      // Get defaults
      // =========================================
      // -
      // Set Access mode
      // =========================================
      if (pMaintain.getMode() == cEnumMode.RETRIEVE) {
         maintain_setAccessModeForRetrieve();
      } else {
         maintain_setAccessModeForEntry();
      }
   }
   
   /**
   * Sets default values for fields in ADD mode step INITIATE of interaction maintain.
   * This method is to ensure that all fields have valid values after clearNOKEY has 
   * been performed.
   */

   public void maintain_initiate_setDefaults() {
   }
   
   /**
   * Sets parameters in step INITIATE of interaction maintain.
   */
   public void maintain_initiate_setParameters() {
      found_FAPIBH = cRefINBNext.getFAPIBH(APIBH, found_FAPIBH, currentCONO, APIBR.getDIVI(), APIBR.getINBN());
      // Reject reason
      pMaintain.SCRE.set().moveLeftPad(APIBR.getSCRE());
      // Reprint after adjustment
      pMaintain.RPAA.set(APIBR.getRPAA());
      // Reject date
      pMaintain.REJD.set(APIBR.getREJD());
      // Text line 1
      pMaintain.SDA1.set().moveLeftPad(APIBR.getSDA1());
      // Text line 2
      pMaintain.SDA2.set().moveLeftPad(APIBR.getSDA2());
      // Text line 3
      pMaintain.SDA3.set().moveLeftPad(APIBR.getSDA3());
      // Entry date
      pMaintain.RGDT.set(APIBR.getRGDT());
      // Change date
      pMaintain.LMDT.set(APIBR.getLMDT());
      // Change ID
      pMaintain.CHID.set().moveLeftPad(APIBR.getCHID());
      // Text ID
      pMaintain.TXID.set(APIBR.getTXID());
      // Change number
      pMaintain.work_CHNO = APIBR.getCHNO();
      pMaintain.work_FAPIBH_CHNO = APIBH.getCHNO();
      // ------------------------
      // Save parameter values - to be able to later check if values changes
      // -
      // Check if main parameters have been set
      // -
   }

   /**
   * Interaction maintain - step VALIDATE.v
   */
   public void do_maintain_validate() {
      // Ensure notifications can be issued again (if the basic premises for the notifications has changed)
      // -
      // Perform validation
      maintain_validate();
      // Save parameter values - to be able to later check if values changes
      // -
   }
   
   /**
   * Interaction maintain - step VALIDATE.
   */
   public void maintain_validate() {
      // Validate main parameters
      // =========================================
      // -
      // Get default values for other parameters
      // =========================================
      // -
      // Set Access mode (access modes might change due entered or defaulted values)
      // =========================================
      // - Access mode doesn't change
      if (pMaintain.isNewEntryContext()) {
         return;
      }
      // Validate other parameters
      // =========================================
      // - No parameters to check
   }

   /**
   * Interaction maintain - step UPDATE.
   */
   public void do_maintain_update() {
      executeTransaction(pMaintain.getTransactionName());
      if (pMaintain.messages.existError()) {
         return;
      }
   }

   /**
   * Perform explicit transaction APS452FncINmaintain
   */
   @Transaction(name=cPXAPS452FncINmaintain.LOGICAL_NAME, primaryTable="FAPIBR") 
   public void transaction_APS452FncINmaintain() {
      // Declaration
      boolean found_FAPIBR = false;
      boolean alreadyLocked = false;
      // Set Invoice batch number to status - Update in progress
      if (!lockInvoiceForUpdate(pMaintain.DIVI.get(), pMaintain.INBN.get(), pMaintain.messages, alreadyLockedByJob, pMaintain.work_FAPIBH_CHNO)) {
         return;
      }
      alreadyLocked = alreadyLockedByJob.getBoolean();
      // Check if update is possible
      // =========================================
      APIBR.setCONO(currentCONO);
      APIBR.setDIVI().moveLeftPad(pMaintain.DIVI.get());
      APIBR.setINBN(pMaintain.INBN.get());
      APIBR.setTRNO(pMaintain.TRNO.get());
      found_FAPIBR = APIBR.CHAIN_LOCK("00", APIBR.getKey("00"));
      // Update of deleted record
      if (pMaintain.getMode() == cEnumMode.CHANGE && !found_FAPIBR) {
         // MSGID=XDE0001 The record has been deleted by another user
         pMaintain.messages.addError(this.DSPGM, "", "XDE0001");
         if (!alreadyLocked) {
            // Set Invoice batch number to status - Unlocked
            unlockInvoice(pMaintain.DIVI.get(), pMaintain.INBN.get());
         }
         return;
      }
      // Update of changed record
      if (pMaintain.getMode() == cEnumMode.CHANGE && found_FAPIBR && pMaintain.work_CHNO != APIBR.getCHNO()) {
         APIBR.UNLOCK("00");
         // MSGID=XUP0001 The record has been changed by user &1
         pMaintain.messages.addError(this.DSPGM, "", "XUP0001", APIBR.getCHID());
         if (!alreadyLocked) {
            // Set Invoice batch number to status - Unlocked
            unlockInvoice(pMaintain.DIVI.get(), pMaintain.INBN.get());
         }
         return;
      }
      // Add to an existing record
      if (pMaintain.getMode() == cEnumMode.ADD && found_FAPIBR) {
         APIBR.UNLOCK("00");
         // MSGID=XAD0001 A record has been entered by user &1
         pMaintain.messages.addError(this.DSPGM, "", "XAD0001", APIBR.getCHID());
         if (!alreadyLocked) {
            // Set Invoice batch number to status - Unlocked
            unlockInvoice(pMaintain.DIVI.get(), pMaintain.INBN.get());
         }
         return;
      }
      // Move parameters to DB-record
      // =========================================
      maintain_update_setValues();
      // Perform update
      // =========================================
      if (!maintain_update_perform(found_FAPIBR)) {
         if (!alreadyLocked) {
            // Set Invoice batch number to status - Unlocked
            unlockInvoice(pMaintain.DIVI.get(), pMaintain.INBN.get());
         }
         return;
      }
      if (!alreadyLocked) {
         // Set Invoice batch number to status - Unlocked
         unlockInvoice(pMaintain.DIVI.get(), pMaintain.INBN.get());
      }
   }
   
   /**
   * Sets database field values in step UPDATE of interaction maintain.
   * Moves values from the parameter list to the database fields.
   */
   public void maintain_update_setValues() {
      // Save record
      FAPIBR_record.setRecord(APIBR);
      // Set fields
      // - No update in this program
   }
   
   /**
   * Effectuate update in interaction maintain.
   * @param found_FAPIBR
   *    Indicates whether the record was found.
   * @return
   *    True if the update was successful.
   */
   public boolean maintain_update_perform(boolean found_FAPIBR) {
      if (found_FAPIBR) {
         boolean recordChanged = !APIBR.equalsRecord(FAPIBR_record);
         if (recordChanged) {
            FAPIBR_setChanged();
            APIBR.UPDAT("00");
         } else {
            APIBR.UNLOCK("00");
         }
       } else {
         FAPIBR_setChanged();
         APIBR.setRGDT(APIBR.getLMDT());
         APIBR.setRGTM(movexTime());
         if (!APIBR.WRITE_CHK("00")) {
            // MSGID=WTR3104 Transaction number &1 already exists
            pMaintain.messages.addError(this.DSPGM, "TRNO", "WTR3104", CRCommon.formatNumForMsg(APIBR.getTRNO()));
            return false;
         }
      }
      // Change data
      pMaintain.RGDT.set(APIBR.getRGDT());
      pMaintain.LMDT.set(APIBR.getLMDT());
      pMaintain.CHID.set().moveLeftPad(APIBR.getCHID());
      return true;
   }
   
   /**
   * Sets access modes for parameters in interaction maintain for RETRIEVE mode.
   */
   public void maintain_setAccessModeForRetrieve() {
      // Set initial access mode for primary keys and OUT to other parameters
      pMaintain.parameters.setAllOUTAccess();
      // Transaction number
      pMaintain.TRNO.setMANDATORYAccess();
      // Disable parameters depending on invoice type.
      maintain_setDISABLEDAccessMode();
   }
   
   /**
   * Sets access modes for parameters in interaction maintain for ADD and CHANGE mode.
   */
   public void maintain_setAccessModeForEntry() {
      // Allow setting of parameter values
      pMaintain.parameters.setAllOPTIONALAccess();
      // Set access mode OUT of primary keys
      pMaintain.DIVI.setOUTAccess();
      // Set access mode OUT of Invoice batch number
      pMaintain.INBN.setOUTAccess();
      // Set access mode OUT of Transaction number
      pMaintain.TRNO.setOUTAccess();
      // Disable parameters depending on invoice type.
      maintain_setDISABLEDAccessMode();
   }
   
   /**
   * Sets access mode DISABLED for some parameters depending on tool type.
   */
   public void maintain_setDISABLEDAccessMode() {
      // Division
      if (!CRCommon.isMultiDivisionCompany() || !CRCommon.isCentralUser()) {
         // If not multidivision or if not central user, then the division field should not be displayed
         pMaintain.DIVI.setDISABLEDAccess();
      }
   }

   /**
   * Interaction settings - step INITIATE.
   */
   public void do_settings_initiate() {
      // Validate primary key parameters
      // =========================================
      // Tool
      pSettings.RESP.validateMANDATORYandConstraints();
      // Return error messages
      if (pSettings.messages.existError()) {
         // Set key parameters to access mode OUT and other parameters to DISABLED.
         pSettings.parameters.setAllDISABLEDAccess();
         return;
      }
      // Read record
      // =========================================
      SYSTR.setCONO(currentCONO);
      SYSTR.setDIVI().clear();
      SYSTR.setPGNM().moveLeftPad("APS452");
      SYSTR.setRESP().move(pSettings.RESP.get());
      if (!SYSTR.CHAIN("00", SYSTR.getKey("00"))) {
         // Set default values
         SYSTR.clearNOKEY("00");
         settings_initiate_setDefaults();
         SYSTR.setPAR1().moveLeft(XXPAR1);
         // MSGID=XRE0103 Record does not exist
         pSettings.messages.addNotification(this.DSPGM, "", "XRE0103");
      }
      // Set parameters
      // =========================================
      settings_initiate_setParameters();
      // Return error messages
      if (pSettings.messages.existError()) {
         return;
      }
      // Set Access mode
      // =========================================
      settings_setAccessMode();
   }
   
   /**
   * Sets default values for fields in step INITIATE of interaction settings.
   */
   public void settings_initiate_setDefaults() {
      // Opening panel
      CSSPIC.move(' ');
      // Panel sequence
      CSDSEQ.moveLeftPad(getDefaultPanels());
      // Sorting order
      CSQTTP.move(1);
   }
   
   /**
   * Sets parameters in step INITIATE of interaction settings.
   */
   public void settings_initiate_setParameters() {
      XXPAR1.moveLeft(SYSTR.getPAR1());
      // Allow setting of parameter values
      pSettings.parameters.setAllOPTIONALAccess();
      // Opening panel
      pSettings.SPIC.set().moveLeftPad(CSSPIC.getChar());
      // Panel sequence
      pSettings.DSEQ.set().moveLeftPad(CSDSEQ);
      // Sorting order
      if (pSettings.QTTP.isBlank()) {
         pSettings.QTTP.set(CSQTTP.getInt());
      } else {
         pSettings.QTTP.validateMANDATORYandConstraints();
         // QTTP is not fully validated since it is validated by the interactive program.
      }
   }

   /**
   * Interaction settings - step VALIDATE.
   */
   public void do_settings_validate() {
      // Validate parameters
      // =========================================
      // Opening panel
      pSettings.SPIC.validateMANDATORYandConstraints();
      // Panel sequence
      pSettings.DSEQ.validateMANDATORYandConstraints();
      CRCommon.checkPanelSequence(pSettings.DSEQ.get(), getAllowedPanels(), pSettings.messages, "DSEQ", this.DSPGM);
   }

   /**
   * Interaction settings - step UPDATE.
   */
   public void do_settings_update() {
      executeTransaction(pSettings.getTransactionName());
      if (pSettings.messages.existError()) {
         return;
      }
   }

   /**
   * Perform explicit transaction APS452FncINsettings
   */
   @Transaction(name=cPXAPS452FncINsettings.LOGICAL_NAME, primaryTable="CSYSTR") 
   public void transaction_APS452FncINsettings() {
      // Declaration
      boolean found_CSYSTR = false;
      // Check if update is possible
      // =========================================
      SYSTR.setCONO(currentCONO);
      SYSTR.setDIVI().clear();
      SYSTR.setPGNM().moveLeftPad("APS452");
      SYSTR.setRESP().move(pSettings.RESP.get());
      found_CSYSTR = SYSTR.CHAIN_LOCK("00", SYSTR.getKey("00"));
      // Move parameters to DB-record
      // =========================================
      settings_update_setValues();
      // Perform update
      // =========================================
      if (!settings_update_perform(found_CSYSTR)) {
         return;
      }
   }

   /**
   * Sets database field values in step UPDATE of interaction settings.
   * Moves values from the parameter list to the database fields.
   */
   public void settings_update_setValues() {
      // Opening panel
      CSSPIC.move(pSettings.SPIC.get());
      // Panel sequence
      CSDSEQ.move(pSettings.DSEQ.get());
      // Sorting order
      CSQTTP.move(pSettings.QTTP.get());
   }
   
   /**
   * Effectuate update in interaction settings.
   * @param found_CSYSTR
   *    Indicates whether the record was found.
   * @return
   *    True if the update was successful.
   */
   public boolean settings_update_perform(boolean found_CSYSTR) {
      SYSTR.setPAR1().moveLeftPad(XXPAR1);
      SYSTR.setLMDT(movexDate());
      if (found_CSYSTR) {
         SYSTR.UPDAT("00");
      } else {
         SYSTR.setRGDT(SYSTR.getLMDT());
         if (!SYSTR.WRITE_CHK("00")) {
            // MSGID=WRE0104 Responsible &1 already exists
            pSettings.messages.addError(this.DSPGM, "", "WRE0104", SYSTR.getRESP());
            return false;
         }
      }
      return true;
   }
   
   /**
   * Sets access mode for interaction settings.
   */
   public void settings_setAccessMode() {
      // Allow setting of parameter values
      pSettings.parameters.setAllOPTIONALAccess();
      // Set access mode OUT of primary keys
      // - No primary keys
      // - Sorting order
      pSettings.QTTP.setOUTAccess();
      // Set MANDATORY access
      // - Opening panel
      pSettings.SPIC.setMANDATORYAccess();
      // - Panel sequence
      pSettings.DSEQ.setMANDATORYAccess();
   }

   /**
   * Sets fields LMDT, CHID, CHNO for the record.
   */
   public void FAPIBR_setChanged() {
      APIBR.setLMDT(movexDate());
      APIBR.setCHID().move(this.DSUSS);
      APIBR.setCHNO(APIBR.getCHNO() + 1);
   }
   
   /**
   * Moves the record from one DB-interface to another.
   * @param to
   *    The DB-interface to move data to.
   * @param from
   *    The DB-interface to move data from.
   */
   public void moveFAPIBR(mvx.db.dta.FAPIBR to, mvx.db.dta.FAPIBR from) {
      FAPIBR_record.reset();
      FAPIBR_record.setMDBRecord(from);
      FAPIBR_record.reset();
      FAPIBR_record.getMDBRecord(to);
   }
   
   /**
   * Sets Invoice batch number to status - Update in progress
   * @param DIVI
   *    Division
   * @param INBN
   *    Supplier invoice batch number
   * @param messages
   *    Container with list of messages
   * @param alreadyLockedByJob
   *    Boolean parameter indicating if already locked by the same job
   * @param currentCHNO
   *    Current change number. Used for checking if the record has been updated by another user.
   *    Send -1 if no such check should be performed.
   * @return
   *    True if the invoice was locked.
   */
   public boolean lockInvoiceForUpdate(MvxString DIVI, long INBN, cCRMessageList messages, MvxString alreadyLockedByJob, int currentCHNO) {
      boolean error = false;
      alreadyLockedByJob.moveLeft(toChar(false));
      // =======================================================
      // Set Invoice batch number to status - Update in progress
      // =======================================================
      pAPS450Fnc_lockForUpdate = get_pAPS450Fnc_lockForUpdate();
      pAPS450Fnc_lockForUpdate.messages.forgetNotifications();
      pAPS450Fnc_lockForUpdate.prepare();
      // Set input parameters
      // - Division
      pAPS450Fnc_lockForUpdate.DIVI.set().moveLeftPad(DIVI);
      // - Invoice batch number
      pAPS450Fnc_lockForUpdate.INBN.set(INBN);
      // - Change number
      pAPS450Fnc_lockForUpdate.CHNO.set(currentCHNO);
      // =========================================
      apCall("APS450Fnc", pAPS450Fnc_lockForUpdate);
      // =========================================
      // Handle messages
      if (pAPS450Fnc_lockForUpdate.messages.existError()) {
         error = true;
         // Return error messages
         messages.addAllErrors(pAPS450Fnc_lockForUpdate.messages);
      }
      alreadyLockedByJob.moveLeft(toChar(pAPS450Fnc_lockForUpdate.alreadyLck.get()));
      // Release resources allocated by the parameter list.
      pAPS450Fnc_lockForUpdate.release();
      return !error;
   }
  
   /**
   * Sets Invoice batch number to status - Unlocked
   * @param DIVI
   *    Division
   * @param INBN
   *    Supplier invoice batch number
   */
   public void unlockInvoice(MvxString DIVI, long INBN) {
      // =============================================
      // Set Invoice batch number to status - Unlocked
      // =============================================
      pAPS450Fnc_unlock = get_pAPS450Fnc_unlock();
      pAPS450Fnc_unlock.messages.forgetNotifications();
      pAPS450Fnc_unlock.prepare();
      // Set input parameters
      // - Division
      pAPS450Fnc_unlock.DIVI.set().moveLeftPad(DIVI);
      // - Invoice batch number
      pAPS450Fnc_unlock.INBN.set(INBN);
      // =========================================
      apCall("APS450Fnc", pAPS450Fnc_unlock);
      // =========================================
      // Release resources allocated by the parameter list.
      pAPS450Fnc_unlock.release();
   }
  
   /**
   * Checks if the invoice is work in progress. Any error messages
   * are returned in the messages parameter.
   * @param DIVI
   *    Division
   * @param INBN
   *    Supplier invoice batch number
   * @param messages
   *    Container with list of messages
   */
   public void checkInvoiceForWIP(MvxString DIVI, long INBN, cCRMessageList messages) {
      // =================================================
      // Check if Invoice batch number is Work in progress
      // =================================================
      pAPS450Fnc_checkIfWIP = get_pAPS450Fnc_checkIfWIP();
      pAPS450Fnc_checkIfWIP.messages.forgetNotifications();
      pAPS450Fnc_checkIfWIP.prepare();
      // Set input parameters
      // - Division
      pAPS450Fnc_checkIfWIP.DIVI.set().moveLeftPad(DIVI);
      // - Invoice batch number
      pAPS450Fnc_checkIfWIP.INBN.set(INBN);
      // =========================================
      apCall("APS450Fnc", pAPS450Fnc_checkIfWIP);
      // =========================================
      // Handle messages
      if (pAPS450Fnc_checkIfWIP.messages.existError()) {
         // Return error messages
         messages.addAllErrors(pAPS450Fnc_checkIfWIP.messages);
      }
      // Release resources allocated by the parameter list.
      pAPS450Fnc_checkIfWIP.release();
   }
   
   /**
   * Initiation of function program
   */
   public void INIT() { 	
      this.PXCONO = currentCONO;
      this.PXDIVI.clear(); 	
      if (FAPIBR_record == null) {
         FAPIBR_record = APIBR.getEmptyRecord();
      }
   }

   /**
   * Throws RuntimeException causing program dump.
   * pListError is called only when no valid parameter list is
   * passed to the function program.
   */
   public void pListError() {
      throw new RuntimeException("No valid parameter list received.");
   }
   
   // Movex MDB definitions
   public mvx.db.dta.CSYSTR SYSTR;
   public mvx.db.dta.FAPIBH APIBH;
   public mvx.db.dta.FAPIBR APIBR;
   public mvx.db.dta.CSYPAR SYPAR;
   
   public void initMDB() {
      SYSTR = (mvx.db.dta.CSYSTR)getMDB("CSYSTR", SYSTR);
      APIBH = (mvx.db.dta.FAPIBH)getMDB("FAPIBH", APIBH);
      APIBR = (mvx.db.dta.FAPIBR)getMDB("FAPIBR", APIBR);
      SYPAR = (mvx.db.dta.CSYPAR)getMDB("CSYPAR", SYPAR);
   }

   // Entry parameters
   @ParameterList
   public cPXAPS452FncINmaintain pMaintain = null;
   @ParameterList
   public cPXAPS452FncINsettings pSettings = null;

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncOPcheckIfWIP get_pAPS450Fnc_checkIfWIP() {
      if (pAPS450Fnc_checkIfWIP == null) {
         cPXAPS450FncOPcheckIfWIP newPlist = new cPXAPS450FncOPcheckIfWIP();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pAPS450Fnc_checkIfWIP.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pAPS450Fnc_checkIfWIP;
      }
   }
  
  
   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncOPlockForUpdate get_pAPS450Fnc_lockForUpdate() {
      if (pAPS450Fnc_lockForUpdate == null) {
         cPXAPS450FncOPlockForUpdate newPlist = new cPXAPS450FncOPlockForUpdate();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pAPS450Fnc_lockForUpdate.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pAPS450Fnc_lockForUpdate;
      }
   }
   
   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncOPunlock get_pAPS450Fnc_unlock() {
      if (pAPS450Fnc_unlock == null) {
         cPXAPS450FncOPunlock newPlist = new cPXAPS450FncOPunlock();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pAPS450Fnc_unlock.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pAPS450Fnc_unlock;
      }
   }
  
   public MvxRecord FAPIBR_record = null;
   public cCRCommon CRCommon = new cCRCommon(this);
   public cCRCalendar CRCalendar = new cCRCalendar(this);
   public cPXAPS450FncOPcheckIfWIP pAPS450Fnc_checkIfWIP = null;
   public cPXAPS450FncOPlockForUpdate pAPS450Fnc_lockForUpdate = null;
   public cPXAPS450FncOPunlock pAPS450Fnc_unlock = null;
   
   public sDSCUCD DSCUCD = new sDSCUCD(this);
   public sAPIDS APIDS = new sAPIDS(this);
   public boolean found_CSYSTR;
   public boolean found_FAPIBH;
   public MvxString messageData = new MvxString(40);
   public MvxString alreadyLockedByJob = new MvxString(1);
   public cPLCHKAD PLCHKAD = new cPLCHKAD(this);
   
   public MvxStruct rXXPAR1 = new MvxStruct(100);
   public MvxString XXPAR1 = rXXPAR1.newString(0, 100);
   public MvxString CSSPIC = rXXPAR1.newChar(0);
   public MvxString CSDSEQ = rXXPAR1.newString(cRefSPIC.length(), cRefPSEQ.length());
   public MvxString CSQTTP = rXXPAR1.newInt(cRefSPIC.length() + cRefPSEQ.length(), cRefQTTP.length());
   public MvxString CSINBN = rXXPAR1.newLong(cRefSPIC.length() + cRefPSEQ.length() + cRefQTTP.length(), cRefINBN.length());

   public String getVarList(java.util.Vector v) {
      super.getVarList(v);
      v.addElement(SYSTR);
      v.addElement(APIBH);
      v.addElement(APIBR);
      v.addElement(SYPAR);
      v.addElement(pMaintain);
      v.addElement(pSettings);
      v.addElement(pAPS450Fnc_checkIfWIP);
      v.addElement(pAPS450Fnc_lockForUpdate);
      v.addElement(pAPS450Fnc_unlock);
      v.addElement(CRCommon);
      v.addElement(CRCalendar);
      v.addElement(rXXPAR1);
      v.addElement(DSCUCD);
      v.addElement(APIDS);
      v.addElement(messageData);
      v.addElement(alreadyLockedByJob);
      v.addElement(PLCHKAD);
      return version;
   }

   public void clearInstance() {
      super.clearInstance();
      found_CSYSTR = false;
      found_FAPIBH = false;
   }

   public final static String PANELS_REQUIRED_FOR_ADD = "E";

   public final static String ALLOWED_PANELS = PANELS_REQUIRED_FOR_ADD + "T-";

   public final static String DEFAULT_PANELS = PANELS_REQUIRED_FOR_ADD + "T";

   /**
   * If you override this method, then you must also override the corresponding method in the interactive program.
   * @return
   *    The panels that are required in the interactive program when 
   *    adding a new record.
   */
   public static String getPanelsRequiredForAdd() {
      return PANELS_REQUIRED_FOR_ADD;
   }

   /**
   * If you override this method, then you must also override the corresponding method in the interactive program.
   * @return
   *    The panels that are allowed to use in the interactive program.
   */
   public static String getAllowedPanels() {
      return ALLOWED_PANELS;
   }

   /**
   * If you override this method, then you must also override the corresponding method in the interactive program.
   * @return
   *    The panels that are defaulted in the interactive program.
   */
   public static String getDefaultPanels() {
      return DEFAULT_PANELS;
   }
   
public final static String _version="15";

public final static String _release="1";

public final static String _spLevel="2";

public final static String _spNumber="";

public final static String _GUID="1923C7FA025A4823A89E5AD2F41CAB7B";

public final static String _tempFixComment="";

public final static String _build="000000000000206";

public final static String _pgmName="APS452Fnc";

   public String getVersion() {
      return _version;
   } //·end of method getVersion

   public String getRelease() {
      return _release;
   } //·end of method getRelease

   public String getSpLevel() {
      return _spLevel;
   } //·end of method getSpLevel

   public String getSpNumber() {
      return _spNumber;
   } //·end of method getSpNumber

   public String getGUID() {
      return _GUID;
   } //·end of method getGUID

   public String getTempFixComment() {
      return _tempFixComment;
   } //·end of method getTempFixComment

   public String getVersionInformation() {
      return _version + '.' + _release + '.' + _spLevel + ':' + _spNumber;
   } //·end of method getVersionInformation

   public String getBuild() {
      return (_version + _release + _build + "      " +  _pgmName + "                                   ").substring(0,34);
   } //·end of method getBuild

   public String [][] getStandardModification() {
      return _standardModifications;
   } //·end of method [][] getStandardModification

  public final static String [][] _standardModifications={};
} 	 	
