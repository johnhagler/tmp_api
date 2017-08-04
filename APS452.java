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
import mvx.runtime.*;
import mvx.db.dta.*;
import mvx.app.util.*;
import mvx.app.plist.*;
import mvx.app.ds.*;
import mvx.dsp.common.GenericDSP;
import mvx.dsp.obj.*;
import mvx.util.*;

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
*<BR><B><FONT SIZE=+2>Wrk: Supplier invoice batch - Reject history</FONT></B><BR><BR>
*
* This interactive program displays reject history by calling function program APS452Fnc.
*/
public class APS452 extends Interactive
{

   /**
   * B-panel - initiate.
   */
   public void PBINZ() {
      ensureReadOfTables();
      // Forget notifications
      if (pMaintain != null) {
         pMaintain.messages.forgetNotifications();
      }
      // Set last panel
      lastPanel = 'B';
      // Position cursor
      if (!isBookmarkProcessing()) {
         if (IN61) {
            DSP.setFocus("W1TRNO");
         }
      }
      //   - Init new Sorting order 	 	
      if (!isBookmarkProcessing()) {
         if (DSP.WWQTTP != XXQTTP) { 	 	
            if (!CRMNGVW.setStandardFromSortingOrder(DSP.WWQTTP, SYVIU, SYVIP, SYSOR)) { 	 	
               //Sorting order not found 	 	
               picSetMethod('D'); 	 	 	 	
               IN60 = true; 	 	 	 	
               DSP.setFocus("WWQTTP");
               // MSGID=WQT0101 Sorting order &1 does not exist 	 	 	
               COMPMQ("WQT0101", formatToString(DSP.WWQTTP, 2)); 	 	 	
               return; 	 	 	 	
            } 	
            CRMNGVW.setDefaultValues(DSP, "WOPAVR", "WOUPVR", DSP.WOPAVR, DSP.WOUPVR, DSP.WWPSEQ, SYSPV, SYSPP, 'B', DSP.WWQTTP, startView, startPanelSequence);
         } 	 	
         //   - Init new View 	 	 	 	
         if (CRMNGVW.initView(DSP.WWQTTP, SYSPV, DSP.WOPAVR, DSP.WOUPVR, this.DSUSS)) { 	 	 	 	
            if (CRMNGVW.PXIN60) { 	 	 	 	
               picSetMethod('D'); 	 	 	
               IN60 = true; 	 	 	
               DSP.setFocus("WOPAVR"); 	 	 	
               COMPMQ(formatToString(CRMNGVW.MSGID), formatToString(DSP.WOPAVR)); 	 	 	 	
               return; 	 	 	
            } 	 	 	
            subfHead(); 	 	
         } 	 	 	 	
      }
      XXPAVR.move(DSP.WOPAVR); 	 	 	 	
      XXUPVR.move(DSP.WOUPVR);
      XXQTTP = DSP.WWQTTP;
      // Init of subfile information
      // - Supplier invoice number
      DSP.WBSINO.moveLeftPad(APIBH.getSINO());
      // - Payee
      DSP.WBSPYN.moveLeftPad(APIBH.getSPYN());
      // - Supplier
      DSP.WBSUNO.moveLeftPad(APIBH.getSUNO());
      // - Invoice date
      DSP.WBIVDT.moveLeft(CRCalendar.convertDate(DSP.WBDIVI, APIBH.getIVDT(), LDAZD.DTFM, ' '));
      // - Currency amount
      this.PXDCCD = cRefCUAM.decimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'J';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = APIBH.getCUAM();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WBCUAM.moveRight(this.PXALPH);
      // - Currency
      DSP.WBCUCD.moveLeftPad(APIBH.getCUCD());
      // - Invoice status
      DSP.WBSUPA = APIBH.getSUPA();
      if (IN61) {
         X8TRNO = DSP.W1TRNO;
      }
      XLTRNO = 0;
      this.WSRGTM = movexDateTime();
      DSP.XBRRNA = 0;
      DSP.XBRRNM = 0;
      if (isBookmarkProcessing()) {
         // Do not fill the subfile at this stage
         return;
      }
      // Clear the subfile
      IN94 = true;
      IN95 = false;
      DSP.clearSFL("BC");
      IN94 = false;
      IN95 = false;
      IN96 = false;
      // Set limit & build subfile, Sorting order 1
      if (IN61) {
         APIBR.setTRNO(DSP.W1TRNO);
         APIBR.SETLL_SCAN("00", APIBR.getKey("00"));
      }
      PBINZ_SETLL(); // exit point for modification
      PBROL();
   }

   /**
   * B-panel - display - initiate.
   */
   public void PBDSP_INZ() {
      // Set last panel
      lastPanel = 'B';
      // Division - only display for central user
      IN21 = !CRCommon.isCentralUser();
   }

   /**
   * B-panel - display - get next Standard view.
   */
   public void PBDSP_F10() { 	
      CRMNGVW.nextStandardView(this, DSP.WOPAVR, DSP.WOUPVR, SYSPV, 'B'); 	 	
      return; 	
   }
  
   /**
   * B-panel - check.
   */
   public void PBCHK() {
      ensureReadOfTables();
      DSP.WBRRNA = 0;
      PBCHK_process();
      PBCHK_ExecuteOption(); // exit point for modification
      // Clear option in case of error
      if (DSP.WBRRNA != 0 && IN60) {
         DSP.clearOption();
         DSP.XBRRNA = DSP.WBRRNA;
         DSP.updateSFL("BS");
      }
   }

   /**
   * B-panel - check - validate and process user action.
   *
   * Turns on IN60 if an error was found.
   */
   public void PBCHK_process() {
      // Clear list update indicator
      boolean listUpdate = false;
      // Check if View created/updated by UI. In that case a refresh is needed 	 	
      CRMNGVW.checkUIAction(DSP, refreshReason, XXPAVR, "WOPAVR", "WOUPVR", DSP.WOPAVR, DSP.WOUPVR,  SYSPV, SYSPP, 'B', DSP.WWQTTP); 	
      // Check sorting order
      if (PBCHK_ValidateInquiry()) {  // exit point for modification
         if (IN60) {
            return;
         }
      } else {
         if (!validSortingOrder(DSP.WWQTTP)) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WWQTTP");
            // MSGID=WQT0101 Sorting order &1 is invalid
            COMPMQ("WQT0101", formatToString(DSP.WWQTTP, 2));
            return;
         }
      }
      CRMNGVW.checkSorting(DSP.WWQTTP, SYVIU); 	 	
      if (CRMNGVW.PXIN60) { 	 	
         picSetMethod('D'); 	 	
         IN60 = true; 	 	
         DSP.setFocus("WWQTTP"); 	 	
         return; 	 	 	 	
      } 	
      // Set sorting order
      if (!getIN(60 + DSP.WWQTTP)) {
         setIndicatorForSortingOrder(DSP.WWQTTP);
         picSetMethod('I');
         return;
      }
      // Check View 	 	
      if (DSP.WOPAVR.NE(XXPAVR) || 	 	 	
          DSP.WOUPVR.NE(XXUPVR) || 	 	
          (DSP.WOPAVR.isBlank() && DSP.WOUPVR.isBlank())) { 	 	 	
         CRMNGVW.PGNM.move(this.DSPGM); 	 	 	 	
         CRMNGVW.checkView(DSP.WWQTTP, SYSPV, DSP.WOPAVR, DSP.WOUPVR); 	 	 	 	 	
         if (CRMNGVW.PXIN60) { 	 	 	 	
            picSetMethod('D'); 	 	
            IN60 = true; 	 	
            DSP.setFocus("WOPAVR"); 	 	 	
            if (CRMNGVW.MSGID.EQ("WPA1103")) { 	 	
               // MSGID=WPA1103 View must be entered 	 	
               COMPMQ("WPA1103"); 	 	
            } else { 	 	
               COMPMQ(formatToString(CRMNGVW.MSGID), formatToString(DSP.WOPAVR)); 	 	 	 	
            } 	
            return; 	 	
         } 	 	
         picSetMethod('I'); 	 	
         return; 	 	 	 	
      }
      // Check panel sequence
      if (!seqCheckSeq(getAllowedPanels(), DSP.WWPSEQ)) {
         return;
      }
      this.CSSQ.moveLeft(this.SEQ);
      // Check list head fields
      // - No field to check
      // Get option and set primary keys
      if (isBookmarkProcessing()) {
         setKeyFieldsInPBCHKFromBookmark();
      } else {
         // Option and keys from subfile head
         DSP.WSOPT2.clear();
         APIBR.setDIVI().moveLeftPad(DSP.WBDIVI);
         APIBR.setINBN(DSP.WBINBN);
         if (IN61) {
            DSP.WSOPT2.move(DSP.WBOPT2);
            APIBR.setTRNO(DSP.W1TRNO);
         }
      }
      // - option and keys from subfile
      if (DSP.WSOPT2.isBlank() && DSP.XBRRNM != 0) {
         boolean endOfSfl = false;
         do {
            DSP.WBRRNA = 0;
            endOfSfl = DSP.readSFL("BS");
            if (!endOfSfl) {
               APIBR.setDIVI().moveLeftPad(DSP.S0DIVI);
               APIBR.setINBN(DSP.S0INBN);
               APIBR.setTRNO(DSP.S0TRNO);
               if (toBoolean(DSP.WSCHG) && DSP.WSOPT2.NE("2 ") && DSP.WSOPT2.NE(" 2") ) {
                  // Field changed in subfile line
                  listUpdate = true;
               }
            }
         } while (DSP.WSOPT2.isBlank() && !endOfSfl && !listUpdate);
      }
      if (pMaintain != null && !listUpdate) {
         pMaintain.messages.forgetNotifications();
      }
      // New start position
      if (DSP.WSOPT2.isBlank() && !listUpdate) {
         if (IN61) {
            if (DSP.W1TRNO != X8TRNO) {
               picSetMethod('I');
               return;
            }
         }
         PBCHK_CheckStartPosition(); // exit point for modification
      }
      // No option given
      if (DSP.WSOPT2.isBlank() && !listUpdate) {
         picSetMethod('D');
         return;
      }
      // Check option - adjust option and check if authorized
      if (listUpdate) {
         this.XXOPT2.move(" 2");
         if (PxCHKoption()) {
            return;
         }
      }
      XXOPT2.clear();
      if (!DSP.WSOPT2.isBlank()) {
         this.XXOPT2.move(DSP.WSOPT2);
         if (PxCHKoption()) {
            return;
         }
      }
      // Reset picture mode indicators IN41 - IN49
      CRCommon.clearMode();
      // Set panel sequence
      seqLoadSavedSeq();
      // Transfer data to calling program
      if (this.XXOPT2.EQ(" 1") && DSP.WBRRNA > 0 && cCRCommon.checkIfTransferData(this)) {
         Bookmark bookmark = getBookmark();
         bookmark.setRecord("FAPIBR", getPrimaryKeyForTable("FAPIBR"));
         bookmark = null;
         LDAZZ.TPGM.move(this.DSPGM);
         picPush("**");
         return;
      }
      // Check if valid option
      if (!DSP.WSOPT2.isBlank() && !PBCHK_ValidateOption()) { // exit point for modification
         if (this.XXOPT2.NE(" 5"))
         {
            picSetMethod('D');
            IN60 = true;
            if (DSP.WBRRNA == 0) {
               DSP.setFocus("WBOPT2");
            }
            // MSGID=WOP0101 Option &1 is invalid
            COMPMQ("WOP0101", this.XXOPT2);
            return;
         }
      }
      // Check if option is authorized
      this.XI = toInt(this.XXOP22.getChar());
      if (this.XI >= 1 && this.XI <= 9 && this.XXOP21.getChar() == ' ' && !toBoolean(this.PXO.charAt(this.XI - 1)) ||
          listUpdate && !toBoolean(this.PXO.charAt(2 - 1)) )
      {
         picSetMethod('D');
         IN60 = true;
         if (DSP.WBRRNA == 0) {
            DSP.setFocus("WBOPT2");
         }
         if (listUpdate) {
            setFocus_on_subfile_B(DSP.WBRRNA);
         }
         // MSGID=XAU0001 You do not have authorization for the selected option
         COMPMQ("XAU0001");
         return;
      }
      // Check key values
      if (APIBR.getTRNO() == 0) {
         picSetMethod('D');
         IN60 = true;
         if (IN61) {
            DSP.setFocus("W1TRNO");
         }
         // MSGID=WTR3102 Transaction number must be entered
         COMPMQ("WTR3102");
         return;
      }
      // Check record
      found_FAPIBR = APIBR.CHAIN("00", APIBR.getKey("00"));
      passFAPIBR = found_FAPIBR;
      IN91 = !found_FAPIBR;
      if (isBookmarkProcessing()) { 	
         validateRecordInPBCHKInCaseOfBookmark(); 	
         if (IN60) { 	
            return;
         } 	
      } 	
      // Add
      if (seqAddModeOk(getPanelsRequiredForAdd())) {
         if (CRCommon.getMode() == cEnumMode.ADD) {
            CRCommon.addPanelsFromSelectedPanelSequence();
            return;
         }
      } else {
         if (DSP.WBRRNA == 0) {
            if (IN61) {
               DSP.setFocus("W1TRNO");
            }
         }
         // MSGID=WTR3104 Transaction number &1 already exists
         COMPMQ("WTR3104", formatToString(APIBR.getTRNO(), cRefTRNO.length()));
         return;
      }
      // Check if exist
      if (!found_FAPIBR) {
         if (listUpdate) {
            setFocus_on_subfile_B(DSP.WBRRNA);
         }
         picSetMethod('D');
         IN60 = true;
         // MSGID=WTR3103 Transaction number &1 does not exist
         COMPMQ("WTR3103", formatToString(APIBR.getTRNO(), cRefTRNO.length()));
         return;
      }
      // Handle update in list
      if (listUpdate) {
         CRCommon.setChangeMode();
         if (updateFromSflB()) {
            pMaintain.messages.forgetNotifications();
            // Prepare to check next subfile line
            picSetMethod('C');
            // Update subfile
            lastPanel = 'B'; // Set last panel
            setFocus_on_subfile_B(DSP.WBRRNA);
            moveToIN(/*dstOffset*/ 1, DSP.S0INDI); // Restore protect indicators
            DSP.clearOption();
            DSP.XBRRNA = DSP.WBRRNA;
            PBMSF();
            DSP.WSCHG = toChar(false); // Indicate no fields changed on sfl-line
            DSP.updateSFL("BS");
            CRCommon.clearMode();
         } else {
            IN60 = true; 	
            picSetMethod('D');  // Display message
            return;
         }
      }
      // Change / Copy / Delete / Display
      if (!seqChangeModeOk() ||
          !seqCopyModeOk() ||
          !seqDeleteModeOk() ||
          !seqDisplayModeOk()) 
      {
         // MSGID=WTR3103 Transaction number &1 does not exist
         COMPMQ("WTR3103", formatToString(APIBR.getTRNO(), cRefTRNO.length()));
         return;
      }
      // Don't display or change if no panels in panel sequence
      if (XXOPT2.EQ(" 2") || XXOPT2.EQ(" 5")) {
         if (picGetPanel() == ' ' || picGetPanel() == '-') {
            picPop();
         }
      }
   }
   
   /**
   * B-panel - update.
   */
   public void PBUPD() {
      ensureReadOfTables();
      lastPanel = 'B'; // Set last panel
      moveToIN(/*dstOffset*/ 1, DSP.S0INDI); // Restore protect indicators
      DSP.clearOption();
      // Next step
      if (DSP.WBRRNA != 0) {
         picSetMethod('C');
      } else {
         picSetMethod('I');
      }
      // No subfile record
      if (DSP.WBRRNA == 0) {
         return;
      }
      // F12 was pressed - stop processing subfile lines
      if (F12 == DSP.X0FKEY) {
         picSetMethod('D');
         this.XLPIC1 = picGetPrevTopPanel();
         // Check if copy aborted.
         if (this.XLPIC1 == 'C') {
            DSP.XBRRNA = DSP.WBRRNA;
            DSP.updateSFL("BS");
            return;
         }
      }
      // Update just option on subfile line if display mode
      if (this.XXOPT2.EQ(" 5")) {
         DSP.XBRRNA = DSP.WBRRNA;
         DSP.updateSFL("BS");
         return;
      }
      // Move new values to subfile (if not copy)
      if (this.XXOPT2.NE(" 3")) {
         if (APIBR.CHAIN("00", APIBR.getKey("00"))) {
            DSP.XBRRNA = DSP.WBRRNA;
         } else {
            IN50 = true;
            IN51 = true;
            DSP.XBRRNA = DSP.XBRRNT;
         }
         PBMSF();
         if (this.XXOPT2.EQ(" 2")) {
            DSP.WSCHG = toChar(false); // Indicate no fields changed on sfl-line
         }
         DSP.updateSFL("BS");
         IN50 = false;
         IN51 = false;
         return;
      }
      // Copy
      DSP.updateSFL("BS");
      // Check if copy was completed
      if (APIBR.CHAIN("00", APIBR.getKey("00"))) {
         DSP.XBRRNM++;
         DSP.XBRRNA = DSP.XBRRNM;
         DSP.WBRRNA = DSP.XBRRNM;
         DSP.WSOPT2.clear();
         PBMSF();
         DSP.WSCHG = toChar(false); // Indicate no fields changed on sfl-line
         DSP.writeSFL("BS");
         IN95 = true;
      }
   }

   /**
   * B-panel - roll subfile.
   */
   public void PBROL() {
      ensureReadOfTables();
      DSP.XBRRNA = DSP.XBRRNM;
      DSP.restPosSFL('B');
      // Restore last position
      if (DSP.XBRRNM > 0) {
         APIBR.setDIVI().moveLeftPad(XLDIVI);
         APIBR.setINBN(XLINBN);
         if (IN61) {
            APIBR.setTRNO(XLTRNO);
            APIBR.SETGT_SCAN("00", APIBR.getKey("00"));
         }
         PBROL_SETGT(); // exit point for modification
      }
      if (IN61) {
         found_FAPIBR = APIBR.READE("00", APIBR.getKey("00", 3));
      }
      PBROL_READE1(); // exit point for modification
      while (found_FAPIBR && this.XXSPGB > 0) {
         if (APIBR.isQualifiedForSFL(WSRGTM)) {
            this.XXSPGB--;
            DSP.XBRRNA++;
            DSP.WBRRNA = DSP.XBRRNA;
            DSP.XBRRNM++;
            XLDIVI.moveLeftPad(APIBR.getDIVI());
            XLINBN = APIBR.getINBN();
            XLTRNO = APIBR.getTRNO();
            DSP.WSOPT2.clear();
            PBROL_PBMSF(); // exit point for modification
            PBMSF();
            DSP.WSCHG = toChar(false); // Indicate no fields changed on sfl-line
            DSP.writeSFL("BS");
         }
         if (IN61) {
            found_FAPIBR = APIBR.READE("00", APIBR.getKey("00", 3));
         }
      }
      // End of subfile
      PBROL_READE2(); // exit point for modification
      if (!found_FAPIBR) {
         IN96 = true;
         DSP.writeEofSFL("BS");
      }
      // If records exists then display the subfile
      if (DSP.XBRRNM > 0) {
         IN95 = true;
         DSP.XBRRNA = DSP.XBRRNM - 1;
         DSP.trunkPosSFL('B');
         DSP.XBRRNA++;
      }
   }

   /**
   * B-panel - move information to subfile fields.
   */
   public void PBMSF() {
      DSP.moveDSintoSFLpre("B"); // Do standard sync for SFL buf
      PBMSF_AfterSFLPre();  // exit point for modification
      // Set hidden key fields
      DSP.S0DIVI.moveLeftPad(APIBR.getDIVI());
      DSP.S0INBN = APIBR.getINBN();
      DSP.S0TRNO = APIBR.getTRNO();
      // Get info from APS452Fnc
      pMaintain = get_pMaintain();
      pMaintain.messages.forgetNotifications();
      pMaintain.prepare(cEnumMode.CHANGE, cEnumStep.INITIATE);
      pMaintain.setQuickMode();
      pMaintain.APIBR = APIBR;
      pMaintain.passFAPIBR = true;
      passExtensionTable(); // exit point for modification
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS452Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
      pMaintain.release();
      // Read tables required in the view
      
      // Override no of decimals 	 	 	
      setOverridingDecimals(); 	 	 	
      // Create subfile line 	 	 	
      CRMNGVW.line();
      // Protect fields
      if (this.PXO.charAt(1) == '0') {
         // Change authority needed for input
         CRMNGVW.protectAllEditFields();
      } else {
         pMaintain.parameters.prepareGetNext();
         for (cParameter param = pMaintain.parameters.getNext(); param != null; 
            param = pMaintain.parameters.getNext()) 
         {
            if (param.isAccessOUTorDISABLED()) {
               CRMNGVW.protectEditField("E8" + param.getName());
            }
         }
      }
      // Set values in virtual fields (&-fields) 	 	 	
      setVirtualFieldValues(); 	 	 	
      DSP.S0QTTP.moveLeft(CRMNGVW.getLine()); 	 	 	
      moveFromIN(DSP.S0INDI, /*fromInd*/ 1); // Save protect indicators
      DSP.S0CHNO = pMaintain.work_CHNO; // Save change number
      PBMSF_BeforeSFLPost(); // exit point for modification
      DSP.moveDSintoSFLpost("B"); // Do standard sync for SFL buf
   }
   
   /**
   * B-panel - prompt.
   */
   public boolean PBPMT() {
      // Next step
      picSetMethod('D');
      DSP.restoreFocus();
      if (!PBPMT_perform()) {
         // MSGID=XF04001 F4 is not permitted in this position on the panel
         COMPMQ("XF04001");
      }
      return true;
   }

   /**
   * B-panel - perform prompting
   * @return
   *    True if prompting was performed.
   */
   public boolean PBPMT_perform() {
      // ----------------------------------------------------------------
      // Prompt sorting order 	 	 	 	
      if (DSP.hasFocus("WWQTTP")) { 	 	 	 	
         DSP.WWQTTP = CRMNGVW.promptSortingOrder(this, DSP, "WWQTTP", DSP.WWQTTP, SYVIU); 	 	 	
         return true; 	
      } 	 	 	
      // ----------------------------------------------------------------
      // Prompt view 	 	 	
      if (DSP.hasFocus("WOPAVR")) { 	 	 	
         CRMNGVW.promptView(this, DSP, "WOPAVR", "WOUPVR", DSP.WOPAVR, DSP.WOUPVR, SYSPV, SYSPP, 'B', DSP.WWQTTP); 	 	 	
         return true;
      } 	 	
      // ----------------------------------------------------------------
      return false;
   }
   
   /**
   * Updates with values entered on the subfile line.
   * Sets focus to the subfile line if an info message is issued.
   * @return
   *    False if an info message is issued.
   */
   public boolean updateFromSflB() {
      boolean infoMessage = false;
      boolean newEntryContext = false;
      // Restore change number
      APIBR.setCHNO(DSP.S0CHNO); 
      // Call APS452Fnc, maintain - mode CHANGE, step INITIATE
      // =========================================
      pMaintain = get_pMaintain();
      pMaintain.prepare(cEnumMode.CHANGE, cEnumStep.INITIATE);
      pMaintain.indicateAutomated();
      // Pass a reference to the record.
      pMaintain.APIBR = APIBR;
      pMaintain.passFAPIBR = true;
      passExtensionTable(); // exit point for modification
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS452Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      infoMessage = handleMessagesInListB(DSP.WBRRNA, pMaintain.messages);
      // Release resources allocated by the parameter list.
      pMaintain.release();
      // Return error message
      if (infoMessage) {
         return false;
      }
      // Call APS452Fnc, maintain - mode CHANGE, step VALIDATE
      // =========================================
      do {
         pMaintain.prepare(cEnumMode.CHANGE, cEnumStep.VALIDATE);
         // Move fields to function parameters
         if (!setParametersFromListB(DSP.WBRRNA)) {
            return false;
         }
         // =========================================
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS452Fnc", pMaintain);
         LDAZZ.FPNM.move(XFPGNM);
         // =========================================
         // Handle messages
         infoMessage = handleMessagesInListB(DSP.WBRRNA, pMaintain.messages);
         newEntryContext = pMaintain.isNewEntryContext();
         // Release resources allocated by the parameter list.
         pMaintain.release();
         // Return error message
         if (infoMessage) {
            return false;
         }
      } while (newEntryContext);
      // Call APS452Fnc, maintain - mode CHANGE, step UPDATE
      // =========================================
      pMaintain.prepare(cEnumMode.CHANGE, cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pMaintain.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pMaintain.messages, transStatus);
      // =========================================
      // Handle messages
      infoMessage = handleMessagesInListB(DSP.WBRRNA, pMaintain.messages);
      // Release resources allocated by the parameter list.
      pMaintain.release();
      // Return error message
      if (infoMessage) {
         return false;
      }
      APIBR.CHAIN("00", APIBR.getKey("00"));
      return true;
   }   

   /**
   * Sets parameters in pMaintain from edit-fields in list B.
   * Issues an info message if an error occurs and sets focus to the field.
   *
   * @param lineNo
   *    Line number in the subfile
   * @return
   *    Returns false an error occurs.
   */
   public boolean setParametersFromListB(int lineNo) {
      // Prepare CRMNGVW
      CRMNGVW.setLine(DSP.S0QTTP);
      moveToIN(/*dstOffset*/ 1, DSP.S0INDI); // Restore protect indicators
      setOverridingDecimals();
      // Division
      if (CRMNGVW.hasField("E8DIVI")) {
         if (!CRMNGVW.isProtected("E8DIVI")) {
            pMaintain.DIVI.set().moveLeftPad(CRMNGVW.getString("E8DIVI"));
         }
      }
      // Invoice batch number
      if (CRMNGVW.hasField("E8INBN")) {
         if (!CRMNGVW.isProtected("E8INBN")) {
            pMaintain.INBN.set(CRMNGVW.getLong("E8INBN"));
         }
      }
      // Transaction number
      if (CRMNGVW.hasField("E8TRNO")) {
         if (!CRMNGVW.isProtected("E8TRNO")) {
            pMaintain.TRNO.set(CRMNGVW.getInt("E8TRNO"));
         }
      }
      return true;
   }
   
   /**
   * Handles messages returned from the function program.
   *
   * @param lineNo
   *    Line number in the subfile
   * @param messages
   *    A reference to the messages.
   * @return
   *    Returns true if a message occured. 
   */
   public boolean handleMessagesInListB(int lineNo, cCRMessageList messages) {
      // handle messages
      // (Don't issue notifications in an open subfile).
      if (messages.exist()) {
         // Check for errors or notifications
         messages.prepareGetNext();
         if (messages.getNextError(CRMessageDS)) {
            // Issue message
            setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
            CRCommon.COMPMQ(CRMessageDS);
            return true;
         }
      }
      return false;
   }
   
   /**
   * Sets focus on a line in the subfile on panel B.
   *
   * @param lineNo
   *    Line number
   */
   public void setFocus_on_subfile_B(int lineNo) { 	
      // Set focus on the line
      DSP.setSubfileFocus("BS", "*ROW", lineNo);
   }
   
   /**
   * Sets focus on a field in a line in the subfile on panel B.
   *
   * @param lineNo
   *    Line number
   * @param FLDI
   *    Field ID
   */
   public void setFocus_on_subfile_B(int lineNo, MvxString FLDI) { 	
      // Set focus on the field
      String fieldName = "E8" + FLDI.toStringRTrim();
      if (CRMNGVW.hasField(fieldName) && !CRMNGVW.isProtected(fieldName)) {
         DSP.setSubfileFocus("BS", fieldName, lineNo); 	
      } else {
         // Try to set focus on first open field instead
         boolean openColumnFound = false;
         MvxString columnName = null;
         for (int column = 0; column < 30; column++) {
            columnName = CRMNGVW.getField(column);
            if (columnName.isBlank()) {
               break;
            }
            if (!CRMNGVW.isProtected(columnName)) {
               openColumnFound = true;
               break;
            }
         }
         if (openColumnFound) {
            DSP.setSubfileFocus("BS", columnName.toStringRTrim(), lineNo); 	
         } else {
            DSP.setSubfileFocus("BS", "*ROW", lineNo);
         }
      }
   } 	

   /**
   * E-panel - initiate.
   */
   public void PEINZ() {
      preparePanelE = true;
      // Call APS452Fnc, maintain - step initiate
      // =========================================
      pMaintain = get_pMaintain();
      pMaintain.prepare(CRCommon.getMode(), cEnumStep.INITIATE);
      // Set key parameters
      if (!passFAPIBR) {
         // Set primary keys
         // - Division
         pMaintain.DIVI.set().moveLeftPad(APIBR.getDIVI());
         // - Invoice batch number
         pMaintain.INBN.set(APIBR.getINBN());
         // - Transaction number
         pMaintain.TRNO.set(APIBR.getTRNO());
      }
      // Pass a reference to the record.
      pMaintain.APIBR = APIBR;
      pMaintain.passFAPIBR = passFAPIBR;
      passFAPIBR = false;
      passExtensionTable(); // exit point for modification
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS452Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      errorOnInitiate = handleMessages(pMaintain.messages, 'E', false);
      // Release resources allocated by the parameter list.
      pMaintain.release();
      // Check whether to abort bookmark
      if (errorOnInitiate && CRCommon.abortBookmark()) {
         SETLR();
      }
   }

   /**
   * E-panel - display - initiate.
   */
   public void PEDSP_INZ() {
      // Set last panel
      lastPanel = 'E';
      // Check if text exists
      IN57 = pMaintain.TXID.get() != 0L;
      // Check if the display fields should be prepared
      if (preparePanelE) {
         preparePanelE = false;
         PEDSP_INZ_prepare();
      }
   }
   
   /**
   * E-panel - display - initiate - prepare display fields.
   * Move function parameters to display fields.
   */
   public void PEDSP_INZ_prepare() {
      // Invoice batch number
      DSP.WEINBN = pMaintain.INBN.get();
      IN01 = pMaintain.INBN.isAccessOUT();
      IN21 = pMaintain.INBN.isAccessDISABLED();
      // Division
      DSP.WEDIVI.moveLeftPad(pMaintain.DIVI.get());
      IN02 = pMaintain.DIVI.isAccessOUT();
      IN22 = pMaintain.DIVI.isAccessDISABLED();
      // Transaction number
      DSP.WETRNO = pMaintain.TRNO.get();
      IN03 = pMaintain.TRNO.isAccessOUT();
      IN23 = pMaintain.TRNO.isAccessDISABLED();
      // Reject reason
      DSP.WESCRE.moveLeftPad(pMaintain.SCRE.get());
      IN04 = pMaintain.SCRE.isAccessOUT();
      IN24 = pMaintain.SCRE.isAccessDISABLED();
      // Reprint after adjustment
      DSP.WERPAA = toInt(pMaintain.RPAA.get());
      IN05 = pMaintain.RPAA.isAccessOUT();
      IN25 = pMaintain.RPAA.isAccessDISABLED();
      // Reject date
      DSP.WEREJD.moveLeft(CRCalendar.convertDate(pMaintain.DIVI.get(), pMaintain.REJD.get(), LDAZD.DTFM, ' '));
      IN06 = pMaintain.REJD.isAccessOUT();
      IN26 = pMaintain.REJD.isAccessDISABLED();
      // Text line 1
      DSP.WESDA1.moveLeftPad(pMaintain.SDA1.get());
      IN07 = pMaintain.SDA1.isAccessOUT();
      IN27 = pMaintain.SDA1.isAccessDISABLED();
      // Text line 2
      DSP.WESDA2.moveLeftPad(pMaintain.SDA2.get());
      IN08 = pMaintain.SDA2.isAccessOUT();
      IN28 = pMaintain.SDA2.isAccessDISABLED();
      // Text line 3
      DSP.WESDA3.moveLeftPad(pMaintain.SDA3.get());
      IN09 = pMaintain.SDA3.isAccessOUT();
      IN29 = pMaintain.SDA3.isAccessDISABLED();
      // Entry date
      DSP.WERGDT.moveLeft(CRCalendar.convertDate_blankDIVI(pMaintain.RGDT.get(), LDAZD.DTFM, LDAZD.DSEP));
      // Change date
      DSP.WELMDT.moveLeft(CRCalendar.convertDate_blankDIVI(pMaintain.LMDT.get(), LDAZD.DTFM, LDAZD.DSEP));
      // Change ID
      DSP.WECHID.moveLeftPad(pMaintain.CHID.get());
   }
   
   /**
   * E-panel - display - roll.
   */
   public void PEDSP_X0E78R() {
      roll(DSP.WETRNO, "WETRNO");
   }

   /**
   * E-panel - check.
   */
   public void PECHK() {
      // If error message in step initiate, then do not proceed any further.
      if (errorOnInitiate) {
         picSetMethod('I');
         return;
      }
      // Call APS452Fnc, maintain - step validate
      // =========================================
      pMaintain.prepare(CRCommon.getMode(), cEnumStep.VALIDATE);
      // Move display fields to function parameters
      if (!PECHK_prepare()) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS452Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
      preparePanelE = true;
      // =========================================
      // Handle messages
      handleMessages(pMaintain.messages, 'E', false);
      if (pMaintain.isNewEntryContext()) {
         picSetMethod('D');
         IN60 = true;
      }
      // Release resources allocated by the parameter list.
      pMaintain.release();
   }
   
   /**
   * E-panel - check - prepare function parameters.
   * Move display fields to function parameters.
   * @return 
   *    True if all the parameters could be prepared.
   */
   public boolean PECHK_prepare() {
      // Reject reason
      if (pMaintain.SCRE.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SCRE.set().moveLeftPad(DSP.WESCRE);
      }
      // Reprint after adjustment
      if (pMaintain.RPAA.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.RPAA.set(DSP.WERPAA);
      }
      // Rejection date
      if (pMaintain.REJD.isAccessMANDATORYorOPTIONAL()) {
         if (!DSP.WEREJD.isBlank()) {
            if (!CRCalendar.convertDate(DSP.WEDIVI, DSP.WEREJD, LDAZD.DTFM)) {
               picSetMethod('D');
               IN60 = true;
               DSP.setFocus("WEREJD");
               // MSGID=WRE5301 Reject date &1 is invalid
               COMPMQ("WRE5301", DSP.WEREJD);
               return false;
            }
            pMaintain.REJD.set(CRCalendar.getDate());
         }
      }
      // Text line 1
      if (pMaintain.SDA1.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SDA1.set().moveLeftPad(DSP.WESDA1);
      }
      // Text line 2
      if (pMaintain.SDA2.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SDA2.set().moveLeftPad(DSP.WESDA2);
      }
      // Text line 3
      if (pMaintain.SDA3.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SDA3.set().moveLeftPad(DSP.WESDA3);
      }
      return true;
   }

   /**
   * E-panel - update.
   */
   public void PEUPD() {
      // Declaration
      boolean error = false;
      // Call APS452Fnc, maintain - step update
      // =========================================
      pMaintain.prepare(CRCommon.getMode(), cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pMaintain.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pMaintain.messages, transStatus);
      preparePanelE = true;
      // =========================================
      // Handle messages
      error = handleMessages(pMaintain.messages, 'E', false);
      // Release resources allocated by the parameter list.
      pMaintain.release();
      // Next step
      if (!error) {
         picSetMethod('I');
         if (CRCommon.getMode() == cEnumMode.ADD) {
            // End of ADD mode
            picSetMethod('I');
            CRCommon.setChangeMode();
         }
         picPush(seqSwitchToNextPanel(), 'I');
      }
   }

   /**
   * E-panel - prompt.
   */
   public boolean PEPMT() {
      // Next step
      picSetMethod('D');
      DSP.restoreFocus();
      if (!PEPMT_perform()) {
         // MSGID=XF04001 F4 is not permitted in this position on the panel
         COMPMQ("XF04001");
      }
      return true;
   }

   /**
   * E-panel - perform prompting
   * @return
   *    True if prompting was performed.
   */
   public boolean PEPMT_perform() {
      // ----------------------------------------------------------------
      // ----------------------------------------------------------------
      return false;
   }
   
   /**
   * Checks if the field ID is blank or if it is located on the panel.
   *
   * @param FLDI
   *    Field ID
   * @return
   *    Returns true if the field is blank or is on the panel.
   */
   public boolean isFieldOnPanel_E(MvxString FLDI) {
      if (FLDI.isBlank()
          || FLDI.EQ("DIVI")
          || FLDI.EQ("INBN")
          || FLDI.EQ("TRNO")
          || FLDI.EQ("SCRE")
          || FLDI.EQ("RPAA")
          || FLDI.EQ("REJD")
          || FLDI.EQ("SDA1")
          || FLDI.EQ("SDA2")
          || FLDI.EQ("SDA3")
          || FLDI.EQ("CHID")
          || FLDI.EQ("LMDT")
          || FLDI.EQ("RGDT")
         )
      {
         return true;
      }
      return false;
   }

   /**
   * Sets focus on a field on the panel.
   *
   * @param FLDI
   *    Field ID
   */
   public void setFocusOnPanel_E(MvxString FLDI) {
      if (FLDI.isBlank()) {
         // No field to set focus on.
      } else if (FLDI.EQ("DIVI")) {  DSP.setFocus("WEDIVI");
      } else if (FLDI.EQ("INBN")) {  DSP.setFocus("WEINBN");
      } else if (FLDI.EQ("TRNO")) {  DSP.setFocus("WETRNO");
      } else if (FLDI.EQ("SCRE")) {  DSP.setFocus("WESCRE");
      } else if (FLDI.EQ("RPAA")) {  DSP.setFocus("WERPAA");
      } else if (FLDI.EQ("REJD")) {  DSP.setFocus("WEREJD");
      } else if (FLDI.EQ("SDA1")) {  DSP.setFocus("WESDA1");
      } else if (FLDI.EQ("SDA2")) {  DSP.setFocus("WESDA2");
      } else if (FLDI.EQ("SDA3")) {  DSP.setFocus("WESDA3");
      } else if (FLDI.EQ("CHID")) {  DSP.setFocus("WECHID");
      } else if (FLDI.EQ("LMDT")) {  DSP.setFocus("WELMDT");
      } else if (FLDI.EQ("RGDT")) {  DSP.setFocus("WERGDT");
      }
   }

   /**
   * P-panel - initiate.
   */
   public void PPINZ() {
      preparePanelP = true;
      // Call APS452Fnc, settings - step initiate
      // =========================================
      pSettings = get_pSettings();
      pSettings.messages.forgetNotifications();
      pSettings.prepare(cEnumStep.INITIATE);
      // Set key parameters
      // - Responsible
      pSettings.RESP.set().move(LDAZD.RESP);
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS452Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      errorOnInitiate = handleMessages(pSettings.messages, 'P', false);
      // Release resources allocated by the parameter list.
      pSettings.release();
   }
   
   /**
   * P-panel - display - initiate.
   */
   public void PPDSP_INZ() {
      // Set last panel
      lastPanel = 'P';
      // Check if the display fields should be prepared
      if (preparePanelP) {
         preparePanelP = false;
         PPDSP_INZ_prepare();
      }
   }
   
   /**
   * P-panel - display - initiate - prepare display fields.
   * Move function parameters to display fields.
   */
   public void PPDSP_INZ_prepare() {
      // Opening panel
      DSP.WWSPIC = pSettings.SPIC.get().getChar();
      // Default panel sequence
      DSP.WWDSEQ.move(pSettings.DSEQ.get());
   }

   /**
   * P-panel - handle F14.
   */
   public void PPDSP_F14() { 	
      //   Create program Meta data 	
      CRMNGVW.createStandardMetaData(LDAZD.CONO, "CrtPgm"); 	
      XXQTTP = -9; 	 	
      XXPAVR.setMax(); 	
      picSetMethod('I'); 	
      return; 	
   }

   /**
   * P-panel - check.
   */
   public void PPCHK() {
      // If error message in step initiate, then do not proceed any further.
      if (errorOnInitiate) {
         picSetMethod('I');
         return;
      }
      // Call APS452Fnc, settings - step validate
      // =========================================
      pSettings.prepare(cEnumStep.VALIDATE);
      // Move display fields to function parameters
      if (!PPCHK_prepare()) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS452Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
      preparePanelP = true;
      // =========================================
      // Handle messages
      handleMessages(pSettings.messages, 'P', false);
      if (pSettings.isNewEntryContext()) {
         picSetMethod('D');
         IN60 = true;
      }
      // Release resources allocated by the parameter list.
      pSettings.release();
   }

   /**
   * P-panel - check - prepare function parameters.
   * Move display fields to function parameters.
   * @return 
   *    True if all the parameters could be prepared.
   */
   public boolean PPCHK_prepare() {
      // Opening panel
      if (pSettings.SPIC.isAccessMANDATORYorOPTIONAL()) {
         pSettings.SPIC.set().move(DSP.WWSPIC);
      }
      // Default panel sequence
      if (pSettings.DSEQ.isAccessMANDATORYorOPTIONAL()) {
         pSettings.DSEQ.set().move(DSP.WWDSEQ);
         if (!seqCheckSeq(getAllowedPanels(), DSP.WWDSEQ)) {
            return false;
         }
      }
      return true;
   }
   
   /**
   * P-panel - update.
   */
   public void PPUPD() {
      // Declaration
      boolean error = false;
      // Call APS452Fnc, settings - step update
      // =========================================
      pSettings.prepare(cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pSettings.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pSettings.messages, transStatus);
      preparePanelP = true;
      // =========================================
      // Handle messages
      error = handleMessages(pSettings.messages, 'P', false);
      // Release resources allocated by the parameter list.
      pSettings.release();
      // Next step
      if (!error) {
         picSetStack(2, DSP.WWSPIC, 'I');
         DSP.WWPSEQ.move(DSP.WWDSEQ);
         this.CSSQ.moveLeft(this.SEQ);
      }
   }
   
   /**
   * Sets focus on a field on the panel.
   *
   * @param FLDI
   *    Field ID
   */
   public void setFocusOnPanel_P(MvxString FLDI) {
      if (FLDI.isBlank()) {
         // No field to set focus on.
      } else if (FLDI.EQ("SPIC")) {  DSP.setFocus("WWSPIC");
      } else if (FLDI.EQ("DSEQ")) {  DSP.setFocus("WWDSEQ");
      }
   }
   
   /**
   * T - panel.
   * Handle program CRS980 - Work with text
   */
   public void PTMAIN() {
      APIBR.CHAIN("00", APIBR.getKey("00"));
      if (F06 == DSP.X0FKEY) {
         DSP.restoreFocus();
      }
      this.PXCONO = APIBR.getCONO();
      this.PXDIVI.clear();
      this.PXFTXH.moveLeft("FSYTXH00");
      this.PXFTXL.moveLeft("FSYTXL00");
      DSINBN.move(APIBR.getINBN());
      DSTRNO.move(APIBR.getTRNO());
      this.PXKFLD.moveLeftPad(DSKFLD);
      this.PXFILE.moveLeft("FAPIBR00");
      this.PXTXID = APIBR.getTXID();
      this.PXLNCD.clear();
      this.PXTXVR.clear();
      this.PXPICC.moveLeft("EI");
      if (CRCommon.getMode() == cEnumMode.RETRIEVE) {
         this.PXOPT2.move(" 5");
      } else {
         this.PXOPT2.move(" 2");
      }
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      // CALL=CRS980 Text. Open
      PXCRS98X.CRS980();
      LDAZZ.FPNM.move(XFPGNM);
      if (APIBR.getTXID() != this.PXTXID) {
         if (APIBR.CHAIN_LOCK("00", APIBR.getKey("00"))) {
            APIBR.setTXID(this.PXTXID);
            APIBR.UPDAT("00");
         }
      }
      if (F06 == DSP.X0FKEY) {
         picPop();
      } else {
         // Next step
         if (F12 == LDAZZ.FKEY) {
            seqPrev();
            picPop();
         } else {
            picSetMethod('D');
            picPush(seqSwitchToNextPanel(), 'I');
         }
      }
   }
   
   /**
   * Roll detail panel.
   * Switches to new record on the detail panels.
   * @param TRNO
   *    The current value.
   * @param fieldName
   *    The name of the display field to set focus on in error message.
   */
   public void roll(int TRNO, String fieldName) {
      // Roll forward
      if (FROLLU == DSP.X0FKEY || F08 == DSP.X0FKEY) {
         APIBR.SETGT("00", APIBR.getKey("00"));
         if (APIBR.READE("00", APIBR.getKey("00", 3))) {
            picSetMethod('I');
            return;
         } else {
            APIBR.SETLL("00", APIBR.getKey("00"));
            if (APIBR.READE("00", APIBR.getKey("00", 3))) {
               picSetMethod('I');
               // MSGID=XRO0001 You have scrolled past first or last record in file
               COMPMQ("XRO0001");
               return;
            }
         }
      }
      // Roll backward
      if (FROLLD == DSP.X0FKEY || F07 == DSP.X0FKEY) {
         APIBR.SETLL("00", APIBR.getKey("00"));
         if (APIBR.REDPE("00", APIBR.getKey("00", 3))) {
            picSetMethod('I');
            return;
         } else {
            APIBR.SETLL("00", APIBR.getKey("00"));
            if (APIBR.READE("00", APIBR.getKey("00", 3))) {
               picSetMethod('I');
               // MSGID=XRO0001 You have scrolled past first or last record in file
               COMPMQ("XRO0001");
               return;
            }
         }
      }
      // Check if keyfield is changed
      if (APIBR.getTRNO() != TRNO) {
         // Save key values
         SSTRNO = APIBR.getTRNO();
         APIBR.setTRNO(TRNO);
         if (APIBR.CHAIN("00", APIBR.getKey("00"))) {
            picSetMethod('I');
            return;
         }
         // Restore key values
         APIBR.setTRNO(SSTRNO);
         picSetMethod('D');
         IN60 = true;
         DSP.setFocus(fieldName);
         // MSGID=WTR3103 Transaction number &1 does not exist
         COMPMQ("WTR3103", formatToString(TRNO, cRefTRNO.length()));
         return;
      } else {
         picSetMethod('I');
         picPush(seqSwitchToNextPanel(), 'I');
         return;
      }
   }
   
   /**
   * Handles messages returned from the function program.
   * @param messages
   *    A reference to the messages.
   * @param panel
   *    The current panel.
   * @param suppressNotification
   *    Indicates if notification messages should be suppressed.
   * @return
   *    Returns true if an error occured. If errors are received during step
   * initiate, then it is not allowed to proceed with steps validate and update.
   */
   public boolean handleMessages(cCRMessageList messages, char panel, boolean suppressNotification) {
      // Declaration
      boolean error = false;
      boolean notification = false;
      boolean clearOption = false;
      // handle messages
      if (messages.exist()) {
         // Check for errors relating to the given panel.
         messages.prepareGetNext();
         while (messages.getNextError(CRMessageDS)) {
            if (panel != 'E'
                || panel == 'E' && isFieldOnPanel_E(CRMessageDS.getPXFLDI())
               )
            {
               error = true;
               break;
            }
         }
         // Check for notifications relating to the given panel.
         if (!error) {
            messages.prepareGetNext();
            while (messages.getNextNotification(CRMessageDS)) {
               if (panel != 'E'
                   || panel == 'E' && isFieldOnPanel_E(CRMessageDS.getPXFLDI())
                  )
               {
                  messages.rememberNotification(CRMessageDS.getPXMSID().toStringRTrim());
                  notification = true;
                  break;
               }
            }
         }
         // Issue message
         if (error || notification) {
            if (error && lastPanel =='B') {
               // Display message in B-panel
               switch (panel) {
                  case 'B':
                     clearOption = true;
                     break;
                  case 'E':
                     clearOption = true;
                     PEDSP_F12();
                     break;
                  case 'P':
                     PPDSP_F12();
                     break;
               }
            } else {
               // Display message in detail panel
               switch (panel) {
                  case 'E':
                     setFocusOnPanel_E(CRMessageDS.getPXFLDI());
                     break;
                  case 'P':
                     setFocusOnPanel_P(CRMessageDS.getPXFLDI());
                     break;
               }
            }
            if (error || notification && !suppressNotification) {
               CRCommon.COMPMQ(CRMessageDS);
            }
         } else if (messages.overflow()){
            // Didn't find any message relating to the given panel due to message overflow
            error = true;
            // MSGID=X_00035 Couldn't validate the panel due to many messages on other panels
            COMPMQ("X_00035");
         }
         if (error || notification && !suppressNotification) {
            if (clearOption && DSP.WBRRNA != 0) {
               moveToIN(/*dstOffset*/ 1, DSP.S0INDI); // Restore protect indicators
               DSP.clearOption();
               DSP.XBRRNA = DSP.WBRRNA;
               DSP.updateSFL("BS");
               setFocus_on_subfile_B(DSP.WBRRNA);
            }
            picSetMethod('D');
            IN60 = true;
         }
      }
      return error;
   }

   /**
   * Refresh request sent by UI, called before the actual panel update request is sent
   * @param reason
   */
   public void refresh(String reason) { 	 	 	 	
      refreshReason.moveLeftPad(reason); 	 	 	 	
   } 	 	 	

   /**
   *    subfHead  - Create subfile heading
   */
   public void subfHead() { 	 	 	
      //  Create headings 	 	 	
      setOverridingHeading(); 	 	 	
      CRMNGVW.heading(); 	 	 	 	
      // Move resulting header to display and communicate the layout to UI 	 	 	 	
      DSP.S0SFH.moveLeft(CRMNGVW.SFL); 	 	 	 	 	
      DSP.WWCOLN.moveLeft(CRMNGVW.COLN); 	 	 	 	
      //  Tell the View manager about positioning fields 	 	 	 	
      setPositioningFields(); 	 	 	
      CRMNGVW.calcSubfLayout(DSP); 	 	 	 	
      CRMNGVW.sendColumnCondition(DSP); 	 	 	 	
      return; 	 	 	
   } 	 	 	

   /**
   *    set Overriding Heading  - set override heading (for &-fields and other...)
   */
   public void setOverridingHeading() { 	 	 	
   } 	 	 	

   /**
   *    set Overriding decimals  - set overriding decimals for  numeric fields
   */
   public void setOverridingDecimals() { 	
   } 	 	 	

   /**
   *    set Positioning Fields  - set positioning fields for subfile
   */
   public void setPositioningFields() { 	 	 	
      if (IN61) {
         if (!CRMNGVW.setPositioningField("E8TRNO", "W1TRNO")) {
            DSP.W1TRNO = 0; 	
         } 	
      }
   } 	 	 	

   /**
   *    set Virtual Field Values  - set values in virtual fields (&-fields)
   */
   public void setVirtualFieldValues() { 	 	 	
   } 	 	 	

   /** 
   * Ensures that tables that are read via cRef-get methods are actually read in the next method call.
   * This is ensured by setting the found-flags to false.
   */
   public void ensureReadOfTables() {
      // No tables read with cRef-get methods.
   }
   
   /**
   * Processes the bookmark.
   * Prepares the program to process the bookmark with the option given in the bookmark.
   *
   * @return
   *    false if the program shall terminate after processing the bookmark.
   */
   public boolean processBookmark() { 	
      // Get bookmark 	
      Bookmark bookmark = getBookmark(); 	
      // Process bookmark 	
      if (bookmark.getTableName().EQ("FAPIBR")) { 	
         // Validate key values in the bookmark 	
         if(!bookmark.isValidIntField("CONO") ||
            !bookmark.isValidStringField("DIVI") ||
            !bookmark.isValidLongField("INBN") ||
            !bookmark.isValidIntField("TRNO")) 	
         { 	
            // MSGID=X_00025 Invalid bookmark 	
            bookmark.setError("X_00025", ""); 	
            return false; 	
         } 	
         // Unpack key values from the bookmark 	
         bookmark_CONO = bookmark.getIntField("CONO");
         bookmark_DIVI.moveLeftPad(bookmark.getStringField("DIVI"));
         bookmark_INBN = bookmark.getLongField("INBN");
         bookmark_TRNO = bookmark.getIntField("TRNO");
         // Check context of the bookmark 	
         if (bookmark_CONO != LDAZD.CONO) { 	
            //   MSGID=X_00026 You must log on to company &1 before using the bookmark 	
            bookmark.setError("X_00026", formatToString(bookmark_CONO, 3)); 	
            return false; 	
         } 	
         // Retrieve record 	
         APIBH.setCONO(bookmark_CONO);
         APIBH.setDIVI().move(bookmark_DIVI);
         APIBH.setINBN(bookmark_INBN); 	
         if (!APIBH.CHAIN("00", APIBH.getKey("00"))) { 	
            APIBH.clearNOKEY("00"); 	
         }
         APIBR.setCONO(bookmark_CONO);
         APIBR.setDIVI().move(bookmark_DIVI);
         APIBR.setINBN(bookmark_INBN);
         APIBR.setTRNO(bookmark_TRNO);
         if (!APIBR.CHAIN("00", APIBR.getKey("00"))) { 	
            APIBR.clearNOKEY("00"); 	
         }
         // Determine inquiry settings 	
         startPanel = bookmark.getStartPanel(picGetPanel()); 	
         startPanelSequence.move(bookmark.getPanelSequence(DSP.WWPSEQ)); 	
         startSortingOrder = bookmark.getInquiryType(DSP.WWQTTP);
         if (!bookmark.getView().isBlank()) {
            startView.moveLeftPad(bookmark.getView());
         } 	
         if (CRMNGVW.setStandardFromSortingOrder(startSortingOrder, SYVIU, SYVIP, SYSOR)) {
            CRMNGVW.setDefaultValues(DSP, "WOPAVR", "WOUPVR", DSP.WOPAVR, DSP.WOUPVR, DSP.WWPSEQ, SYSPV, SYSPP, 'B', startSortingOrder, startView, startPanelSequence);
         }
         startPanelSequence.move(DSP.WWPSEQ);
         DSP.WBDIVI.move(bookmark_DIVI);
         DSP.WBINBN = bookmark_INBN;
         // Process the bookmark
         if (startPanel == 'B' && startSortingOrder == 1) { 	
            // - Set B panel key fields and sorting order
            DSP.W1TRNO = bookmark_TRNO;
            // Init sorting order 	
            DSP.WWQTTP = 1; 	
            XXQTTP = DSP.WWQTTP;
            setIndicatorForSortingOrder(DSP.WWQTTP);
            // Process option 	
            if (!processOptionInBookmark(bookmark, startPanel, startPanelSequence, DSP.WSOPT2)) { 	
               // Option already processed. Stop running application 	
               return false; 	
            }
         } else { 	
            // MSGID=X_00025 Invalid bookmark 	
            bookmark.setError("X_00025", ""); 	
            return false; 	
         } 	   	
      } else { 	
         // MSGID=X_00025 Invalid bookmark 	
         bookmark.setError("X_00025", ""); 	
         return false; 	
      } 	
      return true; 	
   } 	

   /**
   * Called to set key fields when processing bookmark in PBCHK.
   */
   public void setKeyFieldsInPBCHKFromBookmark() {
      APIBR.setDIVI().moveLeftPad(bookmark_DIVI); 	
      APIBR.setINBN(bookmark_INBN);
      APIBR.setTRNO(bookmark_TRNO);
   } 	

   /**
   * Called in PBCHK to validate the selected record before processing it as a bookmark.
   */
   public void validateRecordInPBCHKInCaseOfBookmark() { 	
      // Set subfile hidden key fields from record. 	
      DSP.S0DIVI.moveLeftPad(APIBR.getDIVI());
      DSP.S0INBN = APIBR.getINBN();
      DSP.S0TRNO = APIBR.getTRNO();
      // Nothing to check 	
   } 	

   /**
   * Save start values.
   */
   public void SAVSTR() {
      // Update start values
      pSettings = get_pSettings();
      pSettings.messages.forgetNotifications();
      pSettings.prepare(cEnumStep.INITIATE);
      pSettings.indicateAutomated();
      pSettings.RESP.set().move(LDAZD.RESP);
      SAVSTR_setValues();
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS452Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
      pSettings.prepare(cEnumStep.VALIDATE);
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS452Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
      if (!pSettings.messages.existError()) {
         pSettings.prepare(cEnumStep.UPDATE);
         int transStatus = executeTransaction(pSettings.getTransactionName(), /*returnOnFailure*/ true);
         CRCommon.setDBTransactionErrorMessage(pSettings.messages, transStatus);
      }
      pSettings.release();
   }
   
   /**
   * Set start values before saving.
   */
   public void SAVSTR_setValues() {
      if (!bookmarkInSession(DSPGM)) { 	
         pSettings.QTTP.set(DSP.WWQTTP);
      } 	
   }
   
   /**
   * Set last record - End of program
   */
   public void SETLR() {
      // Clear data in RAM table CSYSPP 	 	
      SYSPP.setCONO(LDAZD.CONO); 	
      SYSPP.setPGNM().moveLeftPad(this.DSPGM); 	
      SYSPP.DELET("00", SYSPP.getKey("00", 2)); 	
      DSP.clearDropDownList("WOPAVR"); 	 	 	
      DSP.clearDropDownList("WOUPVR"); 	
      DSP.clearDropDownList("WWQTTP"); 	
      if (pMaintain != null) {
         pMaintain.messages.forgetNotifications();
      }
      if (pSettings != null) {
         pSettings.messages.forgetNotifications();
      }
      super.SETLR(true);
   }
   
   /**
   * Sets initial settings.
   * Note that some fields already get a default value by the function program.
   */
   public void setInitialSettings() {
      pSettings.SPIC.set().move(LDAZD.SPI1);
      pSettings.SPIC.set().move('B'); // Override SPI1, there is no A-panel in APS452
   }

   /**
   * Validates the sorting order.
   * @param QTTP
   *    Sorting order
   * @return
   *    True if the specified sorting order is valid.
   */
   public boolean validSortingOrder(int QTTP) {
      return QTTP >= 1 && QTTP <=1;
   }

   /**
   * Sets the indicator corresponding to the given sorting order.
   * @param QTTP
   *    Sorting order
   */
   public void setIndicatorForSortingOrder(int QTTP) {
      moveToIN(61, "000000000");  // Clear IN61-69
      moveToIN(60 + QTTP, true);
   }
   
   /**
   * Initiate program.
   */
   public void INIT() {
      // Init keys
      APIBH.setCONO(LDAZD.CONO);
      APIBR.setCONO(LDAZD.CONO);
      this.PXCONO = LDAZD.CONO; 	
      this.PXDIVI.clear(); 	
      // Check authority
      this.PXPGNM.move(this.DSPGM);
      this.PXAUPF.moveRight(LDAZD.AUPF);
      PXAUTCHK.CAUTCHK();
      this.PXO.move(this.PXALOP);
      // - Not allowed to run program
      if (this.PXALPG == 0) {
         // MSGID=XAU0002 You are not authorized to run the program &1
         SRCOMRCM.MSGLVL.moveLeft("*PRV");
         COMPMQ("XAU0002", formatToString(this.DSPGM));
         SETLR();
         return;
      }
      // Check start values
      pSettings = get_pSettings();
      pSettings.messages.forgetNotifications();
      pSettings.prepare(cEnumStep.INITIATE);
      pSettings.indicateAutomated();
      pSettings.RESP.set().move(LDAZD.RESP);
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS452Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
      if (pSettings.messages.exists("XRE0103")) { // Record does not exist
         pSettings.prepare(cEnumStep.VALIDATE);
         setInitialSettings();
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS452Fnc", pSettings);
         LDAZZ.FPNM.move(XFPGNM);
         if (!pSettings.messages.existError()) {
            pSettings.prepare(cEnumStep.UPDATE);
            int transStatus = executeTransaction(pSettings.getTransactionName(), /*returnOnFailure*/ true);
            CRCommon.setDBTransactionErrorMessage(pSettings.messages, transStatus);
         }
      }
      pSettings.release();
      // - Init panel sequence
      this.CSSQ.move(pSettings.DSEQ.get());
      seqLoad();
      // - Init start picture
      picSetPanel(pSettings.SPIC.get().getChar());
      picSetMethod('I');
      // - Init sorting order
      DSP.WWQTTP = pSettings.QTTP.get();
      if (!validSortingOrder(DSP.WWQTTP)) {
         DSP.WWQTTP = 1;
      }
      XXQTTP = -9;
      setIndicatorForSortingOrder(DSP.WWQTTP);
      //   Create program Meta data (if missing) and Create file CSYSPP (View prompt) in QTEMP 	 	
      CRMNGVW.initProgram(LDAZD.CONO, "ChkBildPgm", SYSPP); 	 	
      // Prepare dynamic drop down fields
      CRMNGVW.setDropDownForSorting(DSP, "WWQTTP", SYVIU); 	 	
      // Check for bookmark
      if (bookmarkInSession(DSPGM)) { 	
         // Process bookmark 	
         if (!processBookmark()) { 	
            // Abort after processing the bookmark 	
            SETLR(); 	
            return; 	
         } 	
      } else {
         SETLR();
         return;
      } 	
   }

   String getApName() {
      return "APS452AP";
   }

   // Movex MDB definitions
   public mvx.db.dta.FAPIBH APIBH;
   public mvx.db.dta.FAPIBR APIBR;
   public mvx.db.dta.CSYTAB SYTAB;
   public mvx.db.dta.CSYSPV SYSPV; 	 	
   public mvx.db.dta.CSYSPP SYSPP; 	 	
   public mvx.db.dta.CSYVIU SYVIU; 	 	 	
   public mvx.db.dta.CSYVIP SYVIP;
   public mvx.db.dta.CSYSOR SYSOR;
   // Movex MDB definitions end

   public void initMDB() {
      APIBH = (mvx.db.dta.FAPIBH)getMDB("FAPIBH", APIBH);
      APIBR = (mvx.db.dta.FAPIBR)getMDB("FAPIBR", APIBR);
      SYTAB = (mvx.db.dta.CSYTAB)getMDB("CSYTAB", SYTAB);
      SYSPV = (mvx.db.dta.CSYSPV)getMDB("CSYSPV", SYSPV); 	 	 	
      SYSPP = (mvx.db.dta.CSYSPP)getMDB("CSYSPP", SYSPP); 	 	 	 	
      SYVIU = (mvx.db.dta.CSYVIU)getMDB("CSYVIU", SYVIU); 	 	
      SYVIP = (mvx.db.dta.CSYVIP)getMDB("CSYVIP", SYVIP);
      SYSOR = (mvx.db.dta.CSYSOR)getMDB("CSYSOR", SYSOR);
   }

   public void initDSP() {
      if (DSP == null) {
         DSP = new APS452DSP(this);
      }
   }

   public cPXAPS452FncINmaintain pMaintain = null;
   public cPXAPS452FncINsettings pSettings = null;

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS452FncINmaintain get_pMaintain() {
      if (pMaintain == null) {
         cPXAPS452FncINmaintain newPlist = new cPXAPS452FncINmaintain();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         newPlist.allowUpdateWithErrors(); // This is only set for interactive parameter lists (IN) in interactive programs.
         return newPlist;
      } else {
         pMaintain.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         pMaintain.allowUpdateWithErrors();
         return pMaintain;
      }
   }
  
   /**
   * Calling APS452Fnc with pMaintain as a transaction.
   */
   @Transaction(name=cPXAPS452FncINmaintain.LOGICAL_NAME)
   public void transaction_APS452FncINmaintain() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS452Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
   }
  
  
   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS452FncINsettings get_pSettings() {
      if (pSettings == null) {
         cPXAPS452FncINsettings newPlist = new cPXAPS452FncINsettings();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         newPlist.allowUpdateWithErrors(); // This is only set for interactive parameter lists (IN) in interactive programs.
         return newPlist;
      } else {
         pSettings.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         pSettings.allowUpdateWithErrors();
         return pSettings;
      }
   }

   /**
   * Calling APS452Fnc with pSettings as a transaction.
   */
   @Transaction(name=cPXAPS452FncINsettings.LOGICAL_NAME)
   public void transaction_APS452FncINsettings() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS452Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
   }

   public int bookmark_CONO;
   public MvxString bookmark_DIVI = cRefDIVI.likeDef();
   public long bookmark_INBN;
   public int bookmark_TRNO;
   public char startPanel; 	
   public MvxString startPanelSequence = cRefPSEQ.likeDef();
   public int startSortingOrder;
   public MvxString startView = cRefPAVR.likeDef();
   public int XXQTTP;
   public int X8TRNO;
   public int XLTRNO;
   public int SSTRNO;
   public long XLINBN;
   public char lastPanel;
   public boolean errorOnInitiate;
   public boolean found_FAPIBR;
   public boolean passFAPIBR;
   public boolean preparePanelE;
   public boolean preparePanelP;
   public MvxString XXUPVR = cRefPAVR.likeDef();
   public MvxString XXPAVR = cRefPAVR.likeDef();
   public MvxString refreshReason = new MvxString(8);
   public MvxString XFPGNM = cRefPGNM.likeDef();
   public MvxString XLDIVI = cRefDIVI.likeDef();
   public cPXCRS98X PXCRS98X = new cPXCRS98X(this);
   public sCRMessageDS CRMessageDS = new sCRMessageDS(this);
   public cSRCOMPRI SRCOMPRI = new cSRCOMPRI(this);
   public cCRMNGVW CRMNGVW = new cCRMNGVW(this); 	 	 	
   public cCRCommon CRCommon = new cCRCommon(this);
   public cCRCalendar CRCalendar = new cCRCalendar(this);
   
   public MvxStruct rDSKFLD = new MvxStruct(cRefINBN.length() + cRefTRNO.length());
   public MvxString DSKFLD = rDSKFLD.newString(0, cRefINBN.length() + cRefTRNO.length());
   public MvxString DSINBN = rDSKFLD.newLong(0, cRefINBN.length());
   public MvxString DSTRNO = rDSKFLD.newInt(cRefINBN.length(), cRefTRNO.length());
   
   public APS452DSP DSP;

   public GenericDSP getDSP() {
      return (GenericDSP)DSP;
   }

   /**
   * Called when the program is recycled from the pool to clear class type fields within the program.
   */
   public String getVarList(java.util.Vector v) {
      super.getVarList(v);
      v.addElement(DSP);
      v.addElement(APIBH);
      v.addElement(APIBR);
      v.addElement(SYTAB);
      v.addElement(SYSPV);
      v.addElement(SYSPP);
      v.addElement(SYVIU);
      v.addElement(SYVIP);
      v.addElement(SYSOR);
      v.addElement(pMaintain);
      v.addElement(pSettings);
      v.addElement(startPanelSequence);
      v.addElement(bookmark_DIVI);
      v.addElement(startView);
      v.addElement(XFPGNM);
      v.addElement(PXCRS98X);
      v.addElement(CRMessageDS);
      v.addElement(SRCOMPRI);
      v.addElement(CRMNGVW); 	 	 	 	
      v.addElement(CRCommon);
      v.addElement(CRCalendar);
      v.addElement(XXUPVR); 	 	 	
      v.addElement(XXPAVR); 	 	
      v.addElement(refreshReason);
      v.addElement(XLDIVI);
      v.addElement(rDSKFLD);
      return version;
   }

   /**
   * Called when the program is recycled from the pool to clear primitive type fields within the program.
   */
   public void clearInstance() {
      super.clearInstance();
      bookmark_CONO = 0;
      bookmark_INBN = 0L;
      bookmark_TRNO = 0;
      startPanel = ' '; 	
      startSortingOrder = 0;
      X8TRNO = 0;
      XLTRNO = 0;
      SSTRNO = 0;
      XLINBN = 0L;
      found_FAPIBR = false;
      passFAPIBR = false;
      lastPanel = ' ';
      errorOnInitiate = false;
      XXQTTP = 0;
      preparePanelE = false;
      preparePanelP = false;
   }

   public String getBookmarkTableName() { 	
     return "FAPIBR"; 	
   } 	

   /**
   * If you override the corresponding method in the function program,
   * then you must also override this method. (As is, with no changes).
   * This is needed to make sure the subclass method is called in the function program.
   */
   public String getPanelsRequiredForAdd() {
      return APS452Fnc.getPanelsRequiredForAdd();
   }

   /**
   * If you override the corresponding method in the function program,
   * then you must also override this method. (As is, with no changes).
   * This is needed to make sure the subclass method is called in the function program.
   */
   public String getAllowedPanels() {
      return APS452Fnc.getAllowedPanels();
   }

   /**
   * If you override the corresponding method in the function program,
   * then you must also override this method. (As is, with no changes).
   * This is needed to make sure the subclass method is called in the function program.
   */
   public String getDefaultPanels() {
      return APS452Fnc.getDefaultPanels();
   }

public final static String _version="15";

public final static String _release="1";

public final static String _spLevel="2";

public final static String _spNumber="";

public final static String _GUID="94B364FCF3EA4a58A46CF0724F7ACC6A";

public final static String _tempFixComment="";

public final static String _build="000000000000297";

public final static String _pgmName="APS452";

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
